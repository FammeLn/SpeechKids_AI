package com.speechkids.service;

import com.speechkids.entity.Child;
import com.speechkids.entity.Exercise;
import com.speechkids.entity.ExerciseItem;
import com.speechkids.entity.Notification;
import com.speechkids.entity.User;
import com.speechkids.enums.Difficulty;
import com.speechkids.enums.ExerciseType;
import com.speechkids.enums.Gender;
import com.speechkids.enums.ModelProfileStatus;
import com.speechkids.enums.NotificationType;
import com.speechkids.enums.Role;
import com.speechkids.enums.UserStatus;
import com.speechkids.repository.ChildRepository;
import com.speechkids.repository.ExerciseItemRepository;
import com.speechkids.repository.ExerciseRepository;
import com.speechkids.repository.NotificationRepository;
import com.speechkids.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;
    private final ExerciseItemRepository exerciseItemRepository;
    private final ChildRepository childRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                      ExerciseRepository exerciseRepository,
                      ExerciseItemRepository exerciseItemRepository,
                      ChildRepository childRepository,
                      NotificationRepository notificationRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.exerciseRepository = exerciseRepository;
        this.exerciseItemRepository = exerciseItemRepository;
        this.childRepository = childRepository;
        this.notificationRepository = notificationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User parent = new User();
            parent.setEmail("parent@test.com");
            parent.setFullName("Parent Test");
            parent.setRole(Role.PARENT);
            parent.setStatus(UserStatus.ACTIVE);
            parent.setPasswordHash(passwordEncoder.encode("password123"));
            userRepository.save(parent);

            User therapist = new User();
            therapist.setEmail("therapist@test.com");
            therapist.setFullName("Therapist Test");
            therapist.setRole(Role.THERAPIST);
            therapist.setStatus(UserStatus.ACTIVE);
            therapist.setPasswordHash(passwordEncoder.encode("password123"));
            userRepository.save(therapist);

            User admin = new User();
            admin.setEmail("admin@test.com");
            admin.setFullName("Admin Test");
            admin.setRole(Role.SUPER_ADMIN);
            admin.setStatus(UserStatus.ACTIVE);
            admin.setPasswordHash(passwordEncoder.encode("password123"));
            userRepository.save(admin);

            Child child = new Child();
            child.setParent(parent);
            child.setName("Алина");
            child.setAge(5);
            child.setBirthDate(LocalDate.of(2021, 4, 10));
            child.setGender(Gender.FEMALE);
            child.setNativeLanguage("ru");
            child.setSpeechGoal("Звук Р");
            child.setModelProfileStatus(ModelProfileStatus.COLLECTING);
            childRepository.save(child);

            Notification notification = new Notification();
            notification.setUser(parent);
            notification.setTitle("Новое упражнение");
            notification.setMessage("Логопед назначил занятие на звук Р");
            notification.setType(NotificationType.ASSIGNMENT);
            notification.setRead(false);
            notificationRepository.save(notification);
        }

        if (exerciseRepository.count() == 0) {
            Exercise exercise = new Exercise();
            exercise.setTitle("Назови картинку");
            exercise.setType(ExerciseType.PICTURE_NAMING);
            exercise.setAgeMin(3);
            exercise.setAgeMax(6);
            exercise.setDescription("Ребёнок видит изображение и называет его вслух");
            exercise.setActive(true);
            Exercise savedExercise = exerciseRepository.save(exercise);

            addItem(savedExercise, "/static/images/fish.png", "рыба", "Р,Ы,Б,А", Difficulty.EASY);
            addItem(savedExercise, "/static/images/rocket.png", "ракета", "Р,А,К,Е,Т,А", Difficulty.EASY);
            addItem(savedExercise, "/static/images/hand.png", "рука", "Р,У,К,А", Difficulty.EASY);
            addItem(savedExercise, "/static/images/ball.png", "шар", "Ш,А,Р", Difficulty.EASY);
            addItem(savedExercise, "/static/images/beetle.png", "жук", "Ж,У,К", Difficulty.EASY);
        }
    }

    private void addItem(Exercise exercise, String imageUrl, String targetWord, String phonemes, Difficulty difficulty) {
        ExerciseItem item = new ExerciseItem();
        item.setExercise(exercise);
        item.setImageUrl(imageUrl);
        item.setTargetWord(targetWord);
        item.setTargetPhonemes(phonemes);
        item.setDifficulty(difficulty);
        exerciseItemRepository.save(item);
    }
}
