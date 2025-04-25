package com.dw.artgallery.service;

import com.dw.artgallery.DTO.ArtistDTO;
import com.dw.artgallery.model.Artist;
import com.dw.artgallery.model.Biography;
import com.dw.artgallery.model.User;
import com.dw.artgallery.repository.ArtRepository;
import com.dw.artgallery.repository.ArtistRepository;
import com.dw.artgallery.exception.ResourceNotFoundException;
import com.dw.artgallery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ArtistService {
    private final ArtistRepository artistRepository;
    private final ArtRepository artRepository;
    private final UserRepository userRepository;

    public List<ArtistDTO> getAllArtist(){
        return artistRepository.findAll().stream().map(ArtistDTO::fromEntity).toList();
    }

    public ArtistDTO getArtistById(Long id){
        return artistRepository.findById(id)
                .map(ArtistDTO::fromEntity)
                .orElseThrow(()-> new ResourceNotFoundException("해당 작가/화가가 존재하지 않습니다."));
    }

    public List<ArtistDTO> getArtistByName(String name){
        return artistRepository
                .findByNameLike("%"+name+"%")
                .stream().map(ArtistDTO::fromEntity).toList();
    }

    @Transactional
    public ArtistDTO saveArtist(ArtistDTO artistDTO) {
        Artist artist;

        // 기존작가라면 수정
        if (artistDTO.getId() != null && artistRepository.existsById(artistDTO.getId())) {
            artist = artistRepository.findById(artistDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("작가를 찾을 수 없습니다."));

            artist.setName(artistDTO.getName());
            artist.setProfile_img(artistDTO.getProfile_img());
            artist.setDescription(artistDTO.getDescription());
            artist.setDeleted(artistDTO.isDeleted());

            artist.getBiographyList().clear();
            if (artistDTO.getBiographyList() != null) {
                List<Biography> bioList = artistDTO.getBiographyList().stream()
                        .map(b -> b.toEntity(artist))
                        .toList();
                artist.getBiographyList().addAll(bioList);
            }

        } else {

            User user = userRepository.findById(artistDTO.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("해당 유저를 찾을 수 없습니다."));

            // 새로운 작가라면 추가
            artist = new Artist();
            artist.setName(artistDTO.getName());
            artist.setProfile_img(artistDTO.getProfile_img());
            artist.setDescription(artistDTO.getDescription());
            artist.setDeleted(false);
            artist.setUser(user);
            user.setArtist(true);
            userRepository.save(user);

            if (artistDTO.getBiographyList() != null) {
                List<Biography> bioList = artistDTO.getBiographyList().stream()
                        .map(b -> b.toEntity(artist))
                        .toList();
                artist.setBiographyList(bioList);
            }
        }
        Artist saved = artistRepository.save(artist);
        return ArtistDTO.fromEntity(saved);
    }

    @Transactional
    public String deleteArtist(Long id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("작가를 찾을 수 없습니다."));

        artist.setDeleted(true);
        return "해당 작가를 정상적으로 삭제 처리했습니다.";
    }

}
