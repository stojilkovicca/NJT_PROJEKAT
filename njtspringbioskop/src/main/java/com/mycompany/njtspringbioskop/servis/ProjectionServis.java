
package com.mycompany.njtspringbioskop.servis;

import com.mycompany.njtspringbioskop.dto.impl.ProjectionDto;
import com.mycompany.njtspringbioskop.entity.impl.Hall;
import com.mycompany.njtspringbioskop.entity.impl.Movie;
import com.mycompany.njtspringbioskop.entity.impl.Projection;
import com.mycompany.njtspringbioskop.mapper.impl.ProjectionMapper;
import com.mycompany.njtspringbioskop.repository.impl.ProjectionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectionServis {

    private final ProjectionRepository projectionRepository;
    private final ProjectionMapper projectionMapper;

    @PersistenceContext
    private EntityManager em;

    public ProjectionServis(ProjectionRepository projectionRepository, ProjectionMapper projectionMapper) {
        this.projectionRepository = projectionRepository;
        this.projectionMapper = projectionMapper;
    }

    // ——— CRUD ———

    public List<ProjectionDto> findAll() {
        return projectionRepository.findAll()
                .stream().map(projectionMapper::toDto)
                .collect(Collectors.toList());
    }

    public ProjectionDto findById(Long id) throws Exception {
        return projectionMapper.toDto(projectionRepository.findById(id));
    }

    public ProjectionDto create(ProjectionDto dto) throws Exception {
        validate(dto, false);

        // Reference bez dodatnih selekta kasnije
        Projection p = projectionMapper.toEntity(dto);
        p.setHall(em.getReference(Hall.class, dto.getHallId()));
        p.setMovie(em.getReference(Movie.class, dto.getMovieId()));

        projectionRepository.save(p);
        return projectionMapper.toDto(p);
    }

    public ProjectionDto update(Long id, ProjectionDto dto) throws Exception {
        if (id == null) throw new Exception("ID je obavezan.");
        dto.setId(id);
        validate(dto, true);

        // Provera da postoji
        projectionRepository.findById(id);

        Projection p = projectionMapper.toEntity(dto);
        p.setHall(em.getReference(Hall.class, dto.getHallId()));
        p.setMovie(em.getReference(Movie.class, dto.getMovieId()));

        projectionRepository.save(p);
        return projectionMapper.toDto(p);
    }

    public void deleteById(Long id) {
        projectionRepository.deleteById(id);
    }

    // ——— Korisne metode (jednostavne i praktične) ———

    public List<ProjectionDto> findByHall(Long hallId) {
        return projectionRepository.findByHall(hallId)
                .stream().map(projectionMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ProjectionDto> findByMovie(Long movieId) {
        return projectionRepository.findByMovie(movieId)
                .stream().map(projectionMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ProjectionDto> findInRange(LocalDateTime from, LocalDateTime to) {
        return projectionRepository.findInRange(from, to)
                .stream().map(projectionMapper::toDto)
                .collect(Collectors.toList());
    }

    // ——— Validacija ———
    private void validate(ProjectionDto dto, boolean isUpdate) throws Exception {
        if (dto == null) throw new Exception("DTO je null!");
        if (!isUpdate && dto.getId() != null) throw new Exception("ID se ne prosleđuje pri kreiranju.");
        if (dto.getHallId() == null) throw new Exception("hallId je obavezan.");
        if (dto.getMovieId() == null) throw new Exception("movieId je obavezan.");
        if (dto.getDateTime() == null) throw new Exception("dateTime je obavezan.");
        if (dto.getBasePrice() < 0) throw new Exception("basePrice ne može biti negativan.");
    }
}
