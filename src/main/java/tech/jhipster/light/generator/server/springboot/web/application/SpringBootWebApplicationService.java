package tech.jhipster.light.generator.server.springboot.web.application;

import org.springframework.stereotype.Service;
import tech.jhipster.light.generator.project.domain.Project;
import tech.jhipster.light.generator.server.springboot.web.domain.SpringBootWebService;

@Service
public class SpringBootWebApplicationService {

  private final SpringBootWebService springBootWebService;

  public SpringBootWebApplicationService(SpringBootWebService springBootWebService) {
    this.springBootWebService = springBootWebService;
  }

  public void init(Project project) {
    this.springBootWebService.init(project);
  }

  public void addSpringBootWeb(Project project) {
    springBootWebService.addSpringBootWeb(project);
  }

  public void addSpringBootUndertow(Project project) {
    springBootWebService.addSpringBootUndertow(project);
  }

  public void addExceptionHandler(Project project) {
    springBootWebService.addExceptionHandler(project);
  }
}
