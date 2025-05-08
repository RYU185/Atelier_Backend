package com.dw.artgallery.service;

import com.dw.artgallery.DTO.ArtDTO;
import com.dw.artgallery.DTO.ArtistDTO;
import com.dw.artgallery.model.Art;
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
import java.util.stream.Collectors;

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

    public ArtistDTO getArtistByUserId(String userId) {
        Artist artist = artistRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("작가를 찾을 수 없습니다."));
        return ArtistDTO.fromEntity(artist);
    }

    public List<ArtistDTO> getArtistByName(String name){
        return artistRepository
                .findByNameLike("%"+name+"%")
                .stream().map(ArtistDTO::fromEntity).toList();
    }



    @Transactional
    public ArtistDTO saveArtist(ArtistDTO artistDTO) {
        Artist artist;

        if (artistDTO.getId() != null && artistRepository.existsById(artistDTO.getId())) {
            artist = artistRepository.findById(artistDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("작가를 찾을 수 없습니다."));
        }
        else if (artistRepository.findByUser_UserId(artistDTO.getUserId()).isPresent()) {
            artist = artistRepository.findByUser_UserId(artistDTO.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("작가를 찾을 수 없습니다."));
        }
        else {
            User user = userRepository.findById(artistDTO.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("해당 유저를 찾을 수 없습니다."));

            artist = new Artist();
            artist.setUser(user);
            artist.setDeleted(false);
            user.setArtist(true);
            userRepository.save(user);
        }

        artist.setName(artistDTO.getName());
        artist.setDescription(artistDTO.getDescription());
        if (artistDTO.getProfile_img() != null) {
            artist.setProfile_img(artistDTO.getProfile_img());
        }

        artist.getBiographyList().clear();
        if (artistDTO.getBiographyList() != null) {
            List<Biography> bioList = artistDTO.getBiographyList().stream()
                    .map(b -> b.toEntity(artist))
                    .toList();
            artist.getBiographyList().addAll(bioList);
        }
        return ArtistDTO.fromEntity(artistRepository.save(artist));
    }

    @Transactional
    public String deleteArtist(Long id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("작가를 찾을 수 없습니다."));

        artist.setDeleted(true);
        return "해당 작가를 정상적으로 삭제 처리했습니다.";
    }

}
