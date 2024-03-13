package com.outbrick.colinas.services;

import java.io.IOException;
import java.security.SecureRandom;

import com.outbrick.colinas.utils.ImageUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

import com.outbrick.colinas.dtos.ProfilesDTO;
import com.outbrick.colinas.entities.Profiles;
import com.outbrick.colinas.repositories.ProfilesRepository;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProfilesService {
    
    @Autowired
    private ProfilesRepository profileRepository;

    /*
     * METODO PARA CRIPTOGRAFAR A SENHA DO USUÁRIO
     * METHOD FOR ENCRYPTING THE USER’S PASSWORD
    */
    public String encryptPassword(String profilePassword) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(profilePassword);
    }

    /*
     * GERAR UMA TAG IDENTIFICADORA PARA OS NOVOS USUÁRIOS
     * GENERATE AN IDENTIFIER TAG FOR NEW USERS
    */
    public String generateProfileTag(int length) {
        final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        final SecureRandom random = new SecureRandom();

        StringBuilder stringBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }

        Profiles verifyExistingTag = profileRepository.findProfileByTag(stringBuilder.toString()).orElse(null);
        if(verifyExistingTag != null) return generateProfileTag(length);
        if (stringBuilder.toString().length() < length) return generateProfileTag(length);

        return stringBuilder.toString();
    }

    /*
     * SALVAR USUÁRIOS COM BASE NO JSON RECEBIDO E VALIDA SE O USUÁRIO JÁ EXISTE ANTES DE SALVAR, CASO EXISTA ELE RETORNA NULL
     * SAVE USERS BASED ON THE JSON RECEIVED AND VALIDATES IF THE USER ALREADY EXISTS BEFORE SAVING, IF IT EXISTS IT RETURNS NULL
     */
    @Transactional
    public ProfilesDTO saveProfiles(Profiles profiles, MultipartFile profileImage, MultipartFile profileCV) throws IOException {
        Profiles validProfileExists = profileRepository.existsByProfileEmail(profiles.getProfileEmail());
        if(validProfileExists != null) return null;

        profiles.setProfilePassword(encryptPassword(profiles.getProfilePassword()));
        profiles.setProfileTag(generateProfileTag(8));

        profiles.setProfileImage(ImageUtils.compressImage(profileImage.getBytes()));
        profiles.setProfileCV(ImageUtils.compressImage(profileCV.getBytes()));

        profileRepository.save(profiles);

        Profiles lastAddedProfile = profileRepository.getLastProfileAdded();
        return new ProfilesDTO(
            lastAddedProfile.getProfileId(), 
            lastAddedProfile.getProfileName(), 
            lastAddedProfile.getProfileBirthday(),
            lastAddedProfile.getProfileCreateAt(),
            lastAddedProfile.getProfileStatus(),
            lastAddedProfile.getProfileTag(),
            lastAddedProfile.getProfilePassword(),
            lastAddedProfile.getProfileEmail(), 
            lastAddedProfile.getProfilePhoneNumber(), 
            lastAddedProfile.getProfileInstagram(), 
            lastAddedProfile.getProfileLinkedin(), 
            lastAddedProfile.getProfileOtherSocialMedia(),
            lastAddedProfile.getProfileImage(),
            lastAddedProfile.getProfileDescription(), 
            lastAddedProfile.getProfileProfession(),
            lastAddedProfile.getProfileOccupationArea(),
            lastAddedProfile.getProfileCV(), 
            lastAddedProfile.getProfileProfessionalExperiences(), 
            lastAddedProfile.getProfileEducations(), 
            lastAddedProfile.getProfileSkills(), 
            lastAddedProfile.getProfileState(), 
            lastAddedProfile.getProfileCity(), 
            lastAddedProfile.getProfileAddress(),
            lastAddedProfile.getProfileGender()
        );
    }

    /*
     * RETORNANDO UM USUÁRIO COM BASE EM SEU ID
     * RETURNING A USER BASED ON HIS ID
     */
    public ProfilesDTO getProfilesById(Long profileId) {
        Optional<Profiles> getProfile = profileRepository.findById(profileId);
        return getProfile.map(profiles -> new ProfilesDTO(
                profiles.getProfileId(),
                profiles.getProfileName(),
                profiles.getProfileBirthday(),
                profiles.getProfileCreateAt(),
                profiles.getProfileStatus(),
                profiles.getProfileTag(),
                profiles.getProfilePassword(),
                profiles.getProfileEmail(),
                profiles.getProfilePhoneNumber(),
                profiles.getProfileInstagram(),
                profiles.getProfileLinkedin(),
                profiles.getProfileOtherSocialMedia(),
                profiles.getProfileImage(),
                profiles.getProfileDescription(),
                profiles.getProfileProfession(),
                profiles.getProfileOccupationArea(),
                profiles.getProfileCV(),
                profiles.getProfileProfessionalExperiences(),
                profiles.getProfileEducations(),
                profiles.getProfileSkills(),
                profiles.getProfileState(),
                profiles.getProfileCity(),
                profiles.getProfileAddress(),
                profiles.getProfileGender()
        )).orElse(null);

    }

    /*
     * RETORNANDO A LISTA DTO DE TODOS OS USUÁRIOS CADASTRADOS
     * RETURNING THE LIST OF ALL REGISTERED USERS
     */
    public List<ProfilesDTO> getAllProfiles() {
        return profileRepository.findAll().stream()
            .map(profiles -> new ProfilesDTO(
                profiles.getProfileId(),
                profiles.getProfileName(),
                profiles.getProfileBirthday(),
                profiles.getProfileCreateAt(),
                profiles.getProfileStatus(),
                profiles.getProfileTag(),
                profiles.getProfilePassword(),
                profiles.getProfileEmail(),
                profiles.getProfilePhoneNumber(),
                profiles.getProfileInstagram(),
                profiles.getProfileLinkedin(),
                profiles.getProfileOtherSocialMedia(),
                profiles.getProfileImage() != null ? downloadImage(profiles.getProfileImage()) : null,
                profiles.getProfileDescription(),
                profiles.getProfileProfession(),
                profiles.getProfileOccupationArea(),
                profiles.getProfileCV() != null ? downloadImage(profiles.getProfileCV()) : null,
                profiles.getProfileProfessionalExperiences(),
                profiles.getProfileEducations(),
                profiles.getProfileSkills(),
                profiles.getProfileState(),
                profiles.getProfileCity(),
                profiles.getProfileAddress(),
                profiles.getProfileGender()
            )).collect(Collectors.toList());
    }

    /*
     * CHAMA O METODO DE ATUALIZAR PARA EM SEGUIDA CONVERTER O USUÁRIO <OPTIONAL> EM UM UM USUÁRIO DTO PARA SER RETORNADO
     * CALLS THE UPDATE METHOD TO THEN CONVERT THE USER <OPTIONAL> INTO A DTO USER TO BE RETURNED
     * 
     */
    @Transactional
    public ProfilesDTO getUpdatedProfile(String profileTag, Profiles updatedProfile, MultipartFile profileImage, MultipartFile profileCV) {
        boolean validBeforeUpdate = validProfileBeforeUpdate(updatedProfile.getProfileEmail(), updatedProfile.getProfileTag());
        if (validBeforeUpdate) return null;

        Optional<Profiles> getProfile = updateProfiles(profileTag, updatedProfile, profileImage, profileCV);
        return getProfile.map(profiles -> new ProfilesDTO(
                profiles.getProfileId(),
                profiles.getProfileName(),
                profiles.getProfileBirthday(),
                profiles.getProfileCreateAt(),
                profiles.getProfileStatus(),
                profiles.getProfileTag(),
                profiles.getProfilePassword(),
                profiles.getProfileEmail(),
                profiles.getProfilePhoneNumber(),
                profiles.getProfileInstagram(),
                profiles.getProfileLinkedin(),
                profiles.getProfileOtherSocialMedia(),
                profiles.getProfileImage() != null ? downloadImage(profiles.getProfileImage()) : null,
                profiles.getProfileDescription(),
                profiles.getProfileProfession(),
                profiles.getProfileOccupationArea(),
                profiles.getProfileCV() != null ? downloadImage(profiles.getProfileCV()) : null,
                profiles.getProfileProfessionalExperiences(),
                profiles.getProfileEducations(),
                profiles.getProfileSkills(),
                profiles.getProfileState(),
                profiles.getProfileCity(),
                profiles.getProfileAddress(),
                profiles.getProfileGender()
        )).orElse(null);

    }

    /*
     * METODO PAR ATUALIZAR O USUÁRIO, USANDO STREAM.MAP E RECORD PARA RETORNAR AO BANCO DE DADOS O USUÁRIO ATUALIZADO
     * METHOD TO UPDATE THE USER, USING STREAM.MAP AND RECORD TO RETURN THE UPDATED USER TO THE DATABASE
     */
    @Transactional
    public Optional<Profiles> updateProfiles(String profileTag, Profiles updatedProfile, MultipartFile profileImage, MultipartFile profileCV) {
        Optional<Profiles> existingProfile = profileRepository.findProfileByTag(profileTag);
        if(existingProfile.isPresent()) return existingProfile.map(record -> {
            try {
                record.setProfileName(updatedProfile.getProfileName());
                record.setProfileBirthday(updatedProfile.getProfileBirthday());
                record.setProfileStatus(updatedProfile.getProfileStatus());
                record.setProfileEmail(updatedProfile.getProfileEmail());
                record.setProfilePhoneNumber(updatedProfile.getProfilePhoneNumber());
                record.setProfileInstagram(updatedProfile.getProfileInstagram());
                record.setProfileLinkedin(updatedProfile.getProfileLinkedin());
                record.setProfileOtherSocialMedia(updatedProfile.getProfileOtherSocialMedia());
                if (updatedProfile.getProfileImage() != null) {
                    record.setProfileImage(ImageUtils.compressImage(updatedProfile.getProfileImage()));
                }
                record.setProfileDescription(updatedProfile.getProfileDescription());
                record.setProfileProfession(updatedProfile.getProfileProfession());
                if (updatedProfile.getProfileCV() != null) {
                    record.setProfileCV(ImageUtils.compressImage(updatedProfile.getProfileCV()));
                }
                record.setProfileProfessionalExperiences(updatedProfile.getProfileProfessionalExperiences());
                record.setProfileEducations(updatedProfile.getProfileEducations());
                record.setProfileSkills(updatedProfile.getProfileSkills());
                record.setProfileState(updatedProfile.getProfileState());
                record.setProfileCity(updatedProfile.getProfileCity());
                record.setProfileAddress(updatedProfile.getProfileAddress());
                record.setProfileGender(updatedProfile.getProfileGender());
                return profileRepository.save(record);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return Optional.empty();
    }

    /*
     *  BUSCAR USUÁRIO COM BASE EM SUAS INFORMAÇÕES DE CADASTRO COMO EMAIL E SENHA PARA VALIDAR O LOGIN DO USUÁRIO
     * SEARCH FOR USER BASED ON THEIR REGISTRATION INFORMATION SUCH AS EMAIL AND PASSWORD TO VALIDATE THE USER'S LOGIN
     */
    @Transactional
    public ProfilesDTO findProfileByLogin(String profileEmail, String profilePassword) {
        Profiles findProfile = profileRepository.existsByProfileEmail(profileEmail);
        if(findProfile != null) {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            if (passwordEncoder.matches(profilePassword, findProfile.getProfilePassword())) {
                return new ProfilesDTO(
                    findProfile.getProfileId(), 
                    findProfile.getProfileName(), 
                    findProfile.getProfileBirthday(),
                    findProfile.getProfileCreateAt(),
                    findProfile.getProfileStatus(),
                    findProfile.getProfileTag(),
                    findProfile.getProfilePassword(),
                    findProfile.getProfileEmail(), 
                    findProfile.getProfilePhoneNumber(), 
                    findProfile.getProfileInstagram(), 
                    findProfile.getProfileLinkedin(), 
                    findProfile.getProfileOtherSocialMedia(),
                    findProfile.getProfileImage() != null ? downloadImage(findProfile.getProfileImage()) : null,
                    findProfile.getProfileDescription(), 
                    findProfile.getProfileProfession(),
                    findProfile.getProfileOccupationArea(),
                    findProfile.getProfileCV() != null ? downloadImage(findProfile.getProfileCV()) : null,
                    findProfile.getProfileProfessionalExperiences(), 
                    findProfile.getProfileEducations(), 
                    findProfile.getProfileSkills(), 
                    findProfile.getProfileState(), 
                    findProfile.getProfileCity(), 
                    findProfile.getProfileAddress(), 
                    findProfile.getProfileGender()
                );
            }
        } 

        return null;
    }

    /*
     * BUSCAR USUÁRIO ATRAVES DE SUAS TAGS PARA FACILITAR ENCONTRAR DADOS DE OUTROS PROFISSIONAIS
     * SEARCH FOR USERS THROUGH THEIR TAGS TO FACILITATE FINDING DATA FROM OTHER PROFESSIONALS
     */
    @Transactional
    public ProfilesDTO searchProfileTag(String profileTag) {
        Optional<Profiles> findProfileTag = profileRepository.findProfileByTag(profileTag);
        return findProfileTag.map(profile -> {
                return new ProfilesDTO(
                        profile.getProfileId(),
                        profile.getProfileName(),
                        profile.getProfileBirthday(),
                        profile.getProfileCreateAt(),
                        profile.getProfileStatus(),
                        profile.getProfileTag(),
                        null,
                        profile.getProfileEmail(),
                        profile.getProfilePhoneNumber(),
                        profile.getProfileInstagram(),
                        profile.getProfileLinkedin(),
                        profile.getProfileOtherSocialMedia(),
                        downloadImage(profile.getProfileImage()),
                        profile.getProfileDescription(),
                        profile.getProfileProfession(),
                        profile.getProfileOccupationArea(),
                        downloadImage(profile.getProfileCV()),
                        profile.getProfileProfessionalExperiences(),
                        profile.getProfileEducations(),
                        profile.getProfileSkills(),
                        profile.getProfileState(),
                        profile.getProfileCity(),
                        profile.getProfileAddress(),
                        profile.getProfileGender()
                );
        }).orElse(null);

    }

    @Transactional
    public Optional<Profiles> updatePassword(String profileEmail, String profilePassword) {
        Optional<Profiles> existingProfile = profileRepository.getProfileByEmail(profileEmail);
        if(existingProfile.isPresent()) {
            Profiles profileToUpdate = existingProfile.get();
            profileToUpdate.setProfilePassword(encryptPassword(profilePassword));
            return Optional.of(profileRepository.save(profileToUpdate));
        }
        return Optional.empty();
    }


    /*
     * VERIFICA SE O USUÁRIO ATUALIZADO ESTÁ MUDANDO O EMAIL PARA OUTRO EMAIL QUE JÁ EXISTA CADASTRADO, CASO ISSO ACONTEÇA 
       DEVE RETORNAR NULO PARA QUE O CONTROLLER RESPONDA.
     * CHECK IF THE UPDATED USER IS CHANGING THEIR EMAIL TO ANOTHER EMAIL THAT IS ALREADY REGISTERED, IF THIS HAPPENS
       IT MUST RETURN NULL FOR THE CONTROLLER TO RESPOND.
     */
    public boolean validProfileBeforeUpdate(String profileEmail, String profileTag) {
        Profiles getExistingProfile = profileRepository.existingProfileBeforeUpdate(profileEmail, profileTag);
        return getExistingProfile != null;
    }


    @Transactional
    public byte[] downloadImage(byte[] image) {
        try {
            return ImageUtils.decompressImage(image);
        } catch (DataFormatException | IOException exception) {
            throw new ContextedRuntimeException("Error downloading an image", exception);
        }
    }


    public void deleteProfileByTag(String profileTag) {
        profileRepository.deleteByTag(profileTag);
    }
}
