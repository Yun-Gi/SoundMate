package com.example.SoundMate.service;

import com.example.SoundMate.entity.Genres;
import com.example.SoundMate.repository.GenresRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GenresService {
    
    private final GenresRepository genresRepository;

    //생성자 주입
    public GenresService(GenresRepository genresRepository) {
        this.genresRepository = genresRepository;
    }

    //id로 장르 찾기
    public Optional<Genres> findGenresById(Integer gid) {
        return genresRepository.findById(gid);
    }

    //장르 저장
    public Genres savGenres(Genres genres) {
        return genresRepository.save(genres);
    }
}
