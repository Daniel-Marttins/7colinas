package com.outbrick.colinas.services;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.outbrick.colinas.dtos.ProfilesDTO;
import com.outbrick.colinas.entities.Profiles;
import com.outbrick.colinas.repositories.ProfilesRepository;

@Service
public class ProfilesService {
    
    @Autowired
    private ProfilesRepository profileRepository;

    /*
     * METODO PARA CRIPTOGRAFAR A SENHA DO USUÁRIO
     * METHOD FOR ENCRYPTING THE USER’S PASSWORD
    */
    public String encryptPassword(String profilePassword) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(profilePassword);
        return encodedPassword;
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
    public ProfilesDTO saveProfiles(Profiles profiles) {
        Profiles validProfileExists = profileRepository.existsByProfileEmail(profiles.getProfileEmail());
        if(validProfileExists != null) return null;

        /*
         * CHAMA OS METODOS PARA ENCRIPTOGRAFAR A SENHA E GERAR A TAG 
         * CALLS THE METHODS TO ENCRYPTE THE PASSWORD AND GENERATE THE TAG
         */
        profiles.setProfilePassword(encryptPassword(profiles.getProfilePassword()));
        profiles.setProfileTag(generateProfileTag(8));

        /*
         * CONVERTENDO A IMAGEM EM BASE64 RECEBIDA ATRAVÉS DO @JSON EM UM ARRAY DE BYTES[]
         * CONVERTING THE BASE64 IMAGE RECEIVED THROUGH @JSON INTO AN ARRAY OF BYTES[]
         */
        if (profiles.getProfileImage() != null) {
            profiles.setProfileImage(Base64.getDecoder().decode(profiles.getProfileImage()));
        }

        /*
         * CONVERTENDO O PDF EM BASE64 RECEBIDO DO @JSON EM UM ARRAY DE BYTE[]
         * CONVERTING THE BASE64 PDF RECEIVED FROM @JSON INTO A BYTE ARRAY[]
         */
        if (profiles.getProfileCV() != null) {
            profiles.setProfileCV(Base64.getDecoder().decode(profiles.getProfileCV()));
        }

        profileRepository.save(profiles);

        /*
         * APOS SALVAR JÁ É RETORNANDO UM USUÁRIO EM UM OBJETO DTO, PARA EVITAR CONFLITO DE DESERIALIZAÇÃO NO @JSON
         * AFTER SAVE, A USER IS ALREADY RETURNED IN A DTO OBJECT, TO AVOID DESERIALISATION CONFLICT IN @JSON
         */
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
        if(getProfile.isPresent()) {
            return new ProfilesDTO(
                getProfile.get().getProfileId(), 
                getProfile.get().getProfileName(), 
                getProfile.get().getProfileBirthday(),
                getProfile.get().getProfileCreateAt(),
                getProfile.get().getProfileStatus(),
                getProfile.get().getProfileTag(),
                getProfile.get().getProfilePassword(),
                getProfile.get().getProfileEmail(), 
                getProfile.get().getProfilePhoneNumber(), 
                getProfile.get().getProfileInstagram(), 
                getProfile.get().getProfileLinkedin(), 
                getProfile.get().getProfileOtherSocialMedia(), 
                getProfile.get().getProfileImage(), 
                getProfile.get().getProfileDescription(), 
                getProfile.get().getProfileProfession(), 
                getProfile.get().getProfileCV(), 
                getProfile.get().getProfileProfessionalExperiences(), 
                getProfile.get().getProfileEducations(), 
                getProfile.get().getProfileSkills(), 
                getProfile.get().getProfileState(), 
                getProfile.get().getProfileCity(), 
                getProfile.get().getProfileAddress(), 
                getProfile.get().getProfileGender()
            );
        }

        return null;
    }

    /*
     * RETORNANDO A LISTA DTO DE TODOS OS USUÁRIOS CADASTRADOS
     * RETURNING THE LIST OF ALL REGISTERED USERS
     */
    public List<ProfilesDTO> getAllProfiles() {
        List<ProfilesDTO> allProfiles = profileRepository.findAll().stream()
            .map(profiles -> {
                return new ProfilesDTO(
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
                    profiles.getProfileCV(), 
                    profiles.getProfileProfessionalExperiences(), 
                    profiles.getProfileEducations(), 
                    profiles.getProfileSkills(), 
                    profiles.getProfileState(), 
                    profiles.getProfileCity(), 
                    profiles.getProfileAddress(), 
                    profiles.getProfileGender()
                );
        }).collect(Collectors.toList());

        return allProfiles;  
    }

    /*
     * CHAMA O METODO DE ATUALIZAR PARA EM SEGUIDA CONVERTER O USUÁRIO <OPTIONAL> EM UM UM USUÁRIO DTO PARA SER RETORNADO
     * CALLS THE UPDATE METHOD TO THEN CONVERT THE USER <OPTIONAL> INTO A DTO USER TO BE RETURNED
     * 
     */
    public ProfilesDTO getUpdatedProfile(String profileTag, Profiles updatedProfile) {
        boolean validBeforeUpdate = validProfileBeforeUpdate(updatedProfile.getProfileEmail(), updatedProfile.getProfileTag());
        if (validBeforeUpdate) return null;

        Optional<Profiles> getProfile = updateProfiles(profileTag, updatedProfile);
        if(getProfile.isPresent()) {
            return new ProfilesDTO(
                getProfile.get().getProfileId(), 
                getProfile.get().getProfileName(), 
                getProfile.get().getProfileBirthday(),
                getProfile.get().getProfileCreateAt(),
                getProfile.get().getProfileStatus(),
                getProfile.get().getProfileTag(),
                getProfile.get().getProfilePassword(),
                getProfile.get().getProfileEmail(), 
                getProfile.get().getProfilePhoneNumber(), 
                getProfile.get().getProfileInstagram(), 
                getProfile.get().getProfileLinkedin(), 
                getProfile.get().getProfileOtherSocialMedia(), 
                getProfile.get().getProfileImage(), 
                getProfile.get().getProfileDescription(), 
                getProfile.get().getProfileProfession(), 
                getProfile.get().getProfileCV(), 
                getProfile.get().getProfileProfessionalExperiences(), 
                getProfile.get().getProfileEducations(), 
                getProfile.get().getProfileSkills(), 
                getProfile.get().getProfileState(), 
                getProfile.get().getProfileCity(), 
                getProfile.get().getProfileAddress(), 
                getProfile.get().getProfileGender()
            );
        }

        return null;
    }

    /*
     * METODO PAR ATUALIZAR O USUÁRIO, USANDO STREAM.MAP E RECORD PARA RETORNAR AO BANCO DE DADOS O USUÁRIO ATUALIZADO
     * METHOD TO UPDATE THE USER, USING STREAM.MAP AND RECORD TO RETURN THE UPDATED USER TO THE DATABASE
     */
    public Optional<Profiles> updateProfiles(String profileTag, Profiles updatedProfile) {
        Optional<Profiles> existingProfile = profileRepository.findProfileByTag(profileTag);
        if(existingProfile.isPresent()) return existingProfile.map(record -> {
            record.setProfileName(updatedProfile.getProfileName()); 
            record.setProfileBirthday(updatedProfile.getProfileBirthday());
            record.setProfileStatus(updatedProfile.getProfileStatus());
            record.setProfilePassword(encryptPassword(updatedProfile.getProfilePassword()));
            record.setProfileEmail(updatedProfile.getProfileEmail()); 
            record.setProfilePhoneNumber(updatedProfile.getProfilePhoneNumber()); 
            record.setProfileInstagram(updatedProfile.getProfileInstagram()); 
            record.setProfileLinkedin(updatedProfile.getProfileLinkedin()); 
            record.setProfileOtherSocialMedia(updatedProfile.getProfileOtherSocialMedia());
            record.setProfileImage(updatedProfile.getProfileImage()); 
            record.setProfileDescription(updatedProfile.getProfileDescription()); 
            record.setProfileProfession(updatedProfile.getProfileProfession());
            record.setProfileCV(updatedProfile.getProfileCV()); 
            record.setProfileProfessionalExperiences(updatedProfile.getProfileProfessionalExperiences()); 
            record.setProfileEducations(updatedProfile.getProfileEducations()); 
            record.setProfileSkills(updatedProfile.getProfileSkills()); 
            record.setProfileState(updatedProfile.getProfileState()); 
            record.setProfileCity(updatedProfile.getProfileCity()); 
            record.setProfileAddress(updatedProfile.getProfileAddress());
            record.setProfileGender(updatedProfile.getProfileGender());
            
            return profileRepository.save(record);
        });
        return null;
    }

    /*
     *  BUSCAR USUÁRIO COM BASE EM SUAS INFORMAÇÕES DE CADASTRO COMO EMAIL E SENHA PARA VALIDAR O LOGIN DO USUÁRIO
     * SEARCH FOR USER BASED ON THEIR REGISTRATION INFORMATION SUCH AS EMAIL AND PASSWORD TO VALIDATE THE USER'S LOGIN
     */
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
                    findProfile.getProfileImage(), 
                    findProfile.getProfileDescription(), 
                    findProfile.getProfileProfession(), 
                    findProfile.getProfileCV(), 
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
    public ProfilesDTO searchProfileTag(String profileTag) {
        Optional<Profiles> findProfileTag = profileRepository.findProfileByTag(profileTag);
        if(findProfileTag.isPresent()) {
            return new ProfilesDTO(
                findProfileTag.get().getProfileId(), 
                findProfileTag.get().getProfileName(), 
                findProfileTag.get().getProfileBirthday(),
                findProfileTag.get().getProfileCreateAt(),
                findProfileTag.get().getProfileStatus(),
                findProfileTag.get().getProfileTag(),
                null,
                findProfileTag.get().getProfileEmail(), 
                findProfileTag.get().getProfilePhoneNumber(), 
                findProfileTag.get().getProfileInstagram(), 
                findProfileTag.get().getProfileLinkedin(), 
                findProfileTag.get().getProfileOtherSocialMedia(), 
                findProfileTag.get().getProfileImage(), 
                findProfileTag.get().getProfileDescription(), 
                findProfileTag.get().getProfileProfession(), 
                findProfileTag.get().getProfileCV(), 
                findProfileTag.get().getProfileProfessionalExperiences(), 
                findProfileTag.get().getProfileEducations(), 
                findProfileTag.get().getProfileSkills(), 
                findProfileTag.get().getProfileState(), 
                findProfileTag.get().getProfileCity(), 
                findProfileTag.get().getProfileAddress(), 
                findProfileTag.get().getProfileGender()
            );
        } 

        return null;
    }

    /*
     * VERIFICA SE O USUÁRIO ATUALIZADO ESTÁ MUDANDO O EMAIL PARA OUTRO EMAIL QUE JÁ EXISTA CADASTRADO, CASO ISSO ACONTEÇA 
       DEVE RETORNAR NULO PARA QUE O CONTROLLER RESPONDA.
     * CHECK IF THE UPDATED USER IS CHANGING THEIR EMAIL TO ANOTHER EMAIL THAT IS ALREADY REGISTERED, IF THIS HAPPENS
       IT MUST RETURN NULL FOR THE CONTROLLER TO RESPOND.
     */
    public boolean validProfileBeforeUpdate(String profileEmail, String profileTag) {
        Profiles getExistingProfile = profileRepository.existingProfileBeforeUpdate(profileEmail, profileTag);
        if(getExistingProfile != null) return true;
        return false;
    }
}
