package app.service;

import app.api.ApiReader;
import app.dao.*;
import app.dto.*;
import app.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MovieServiceThreaded {
    private static final Logger logger = LoggerFactory.getLogger(MovieServiceThreaded.class);
    private final ApiReader apiReader;
    private final MovieDAO movieDAO;
    private final GenreDAO genreDAO;
    private final ActorDAO actorDAO;
    private final DirectorDAO directorDAO;

    public MovieServiceThreaded(ApiReader apiReader, MovieDAO movieDAO, GenreDAO genreDAO,
                                ActorDAO actorDAO, DirectorDAO directorDAO) {
        this.apiReader = apiReader;
        this.movieDAO = movieDAO;
        this.genreDAO = genreDAO;
        this.actorDAO = actorDAO;
        this.directorDAO = directorDAO;
    }

    public void fetchAndSaveAllDanishMoviesParallel(String fromDate, String toDate, int numberOfThreads) {
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        AtomicInteger saved = new AtomicInteger(0);
        int page = 1;
        int totalPages = 1;

        while (page <= totalPages) {
            String url = apiReader.buildDiscoverUrl(page, fromDate, toDate);
            MovieResultDTO result = apiReader.convertFromJson(apiReader.readAPI(url), MovieResultDTO.class);
            totalPages = result.totalPages();

            logger.info("Side {}/{} — sender {} film til trådpuljen", page, totalPages, result.results().size());

            for (MovieDTO movieSummary : result.results()) {
                executor.submit(() -> {
                    try {
                        if (movieDAO.findByTmdbId(movieSummary.id()).isPresent()) {
                            return;
                        }
                        fetchAndSaveOneMovie(movieSummary.id());
                        saved.incrementAndGet();
                    } catch (Exception e) {
                        logger.warn("Springer film {} ({}) over: {}", movieSummary.id(), movieSummary.title(), e.getMessage());
                    }
                });
            }
            page++;
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        logger.info("Færdig (parallel). Gemt: {} film.", saved.get());
    }

    private void fetchAndSaveOneMovie(int tmdbMovieId) {
        MovieDetailsDTO details = apiReader.convertFromJson(
                apiReader.readAPI(apiReader.buildMovieDetailsUrl(tmdbMovieId)), MovieDetailsDTO.class);
        CreditsDTO credits = apiReader.convertFromJson(
                apiReader.readAPI(apiReader.buildCreditsUrl(tmdbMovieId)), CreditsDTO.class);

        saveMovieFromDto(details, credits);
    }

    private void saveMovieFromDto(MovieDetailsDTO details, CreditsDTO credits) {
        Movie movie = new Movie();
        movie.setTmdbId(details.id());
        movie.setTitle(details.title());
        if (details.releaseDate() != null && !details.releaseDate().isBlank()) {
            movie.setReleaseDate(LocalDate.parse(details.releaseDate()));
        }
        movie.setVoteAverage(details.voteAverage());
        movie.setPopularity(details.popularity());

        if (details.genres() != null) {
            for (GenreDTO genreDto : details.genres()) {
                movie.getGenres().add(findOrCreateGenre(genreDto));
            }
        }

        if (credits.crew() != null) {
            Optional<CrewMemberDTO> directorDto = credits.crew().stream()
                    .filter(c -> "Director".equalsIgnoreCase(c.job()))
                    .findFirst();
            directorDto.ifPresent(dto -> movie.setDirector(findOrCreateDirector(dto)));
        }

        Movie savedMovie = movieDAO.create(movie);

        if (credits.cast() != null) {
            for (CastMemberDTO castDto : credits.cast()) {
                Actor actor = findOrCreateActor(castDto);
                MovieCast movieCast = new MovieCast();
                movieCast.setMovie(savedMovie);
                movieCast.setActor(actor);
                movieCast.setCharacter(castDto.character());
                savedMovie.getCast().add(movieCast);
            }
            movieDAO.update(savedMovie);
        }
    }

    private synchronized Genre findOrCreateGenre(GenreDTO dto) {
        return genreDAO.findByTmdbId(dto.id()).orElseGet(() -> {
            Genre genre = new Genre();
            genre.setTmdbId(dto.id());
            genre.setName(dto.name());
            return genreDAO.create(genre);
        });
    }

    private synchronized Actor findOrCreateActor(CastMemberDTO dto) {
        return actorDAO.findByTmdbId(dto.id()).orElseGet(() -> {
            Actor actor = new Actor();
            actor.setTmdbId(dto.id());
            actor.setName(dto.name());
            return actorDAO.create(actor);
        });
    }

    private synchronized Director findOrCreateDirector(CrewMemberDTO dto) {
        return directorDAO.findByTmdbId(dto.id()).orElseGet(() -> {
            Director director = new Director();
            director.setTmdbId(dto.id());
            director.setName(dto.name());
            return directorDAO.create(director);
        });
    }
}