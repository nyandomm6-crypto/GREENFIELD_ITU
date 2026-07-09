package itu.greenField.service;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class FaqDataSeeder implements ApplicationRunner {

    private final FaqService faqService;

    public FaqDataSeeder(FaqService faqService) {
        this.faqService = faqService;
    }

    @Override
    public void run(ApplicationArguments args) {
        faqService.seedDefaultFaqsIfEmpty();
    }
}
