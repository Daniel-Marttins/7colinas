package com.outbrick.colinas.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Profiles {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileId;
    private String profileName;
    private Date profileBirthday;
    private Date profileCreateAt;
    private String profileStatus;
    private String profileTag;
    private String profilePassword;
    private String profileEmail;
    private String profilePhoneNumber;
    private String profileInstagram;
    private String profileLinkedin;
    private String profileOtherSocialMedia;
    @Lob
    private byte[] profileImage;
    private String profileDescription;
    private String profileProfession;
    @Lob
    private byte[] profileCV;
    private List<String> profileProfessionalExperiences = new ArrayList<>();
    private List<String> profileEducations = new ArrayList<>();
    private List<String> profileSkills = new ArrayList<>();
    private String profileState;
    private String profileCity;
    private String profileAddress;
    private String profileGender;
    
}
