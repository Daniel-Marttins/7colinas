package com.outbrick.colinas.dtos;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProfilesDTO(
    Long profileId,
    String profileName,
    Date profileBirthday,
    Date profileCreateAt,
    String profileStatus,
    String profileTag,
    String profilePassword,
    String profileEmail,
    String profilePhoneNumber,
    String profileInstagram,
    String profileLinkedin,
    String profileOtherSocialMidia,
    byte[] profileImage,
    String profileDescription,
    String profilProfession,
    byte[] profileCV,
    List<String> profileProfessionalExperiences,
    List<String> profileEducations,
    List<String> profileSkills,
    String profileState,
    String profileCity,
    String profileAdress,
    String profileGender
) {}
