package ch.zhaw.stellarcompass.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import ch.zhaw.stellarcompass.model.Lesson;
import ch.zhaw.stellarcompass.model.Subject;
import ch.zhaw.stellarcompass.repository.LessonRepository;
import ch.zhaw.stellarcompass.repository.SubjectRepository;

/**
 * This Class is used for bootstrapping the application.
 * It is automatically executed when Spring Boot starts.
 * Purpose: Ensure that the database is never empty, so that frontend development
 * and tests are possible immediately without having to manually enter data via Postman.
 */

@Component
@Profile("!test") // Do not run this initializer when the 'test' profile is active since I use mockito there.
//this data initializer is only for development, fronted and manual testing purposes
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Override
    public void run(String... args) throws Exception {
        // 1. Check: Are there already subjects? If yes, do nothing (database protection).
        if (subjectRepository.count() > 0) {
            System.out.println("DataInitializer: Data already present. Skipping initialization.");
            return;
        }

        System.out.println("DataInitializer: Database is empty. Starting initialization of test data...");
        // 2. Create subjects
        Subject math = new Subject("Mathematics", "Basics of algebra and geometry");
        // We save the object to get the generated ID
        math = subjectRepository.save(math);
        Subject history = new Subject("History", "World history of the 20th century");
        history = subjectRepository.save(history);

        // 3. Create lessons and link them
        // Lessons for Math
        Lesson l1 = new Lesson("Introduction to Numbers", "What are natural numbers?", math.getId());
        l1.setContentType("TEXT");
        lessonRepository.save(l1);

        Lesson l2 = new Lesson("Adding & Subtracting", "Learn basic arithmetic.", math.getId());
        l2.setVideoUrl("https://youtube.com/watch?v=example");
        l2.setContentType("VIDEO");
        lessonRepository.save(l2);

        // Lessons for History
        Lesson l3 = new Lesson("The Cold War", "Summary of events.", history.getId());
        l3.setContentType("TEXT");
        lessonRepository.save(l3);

        System.out.println("DataInitializer: Test data successfully loaded!");
    }
}