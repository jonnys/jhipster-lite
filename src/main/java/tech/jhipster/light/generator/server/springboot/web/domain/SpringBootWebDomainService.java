package tech.jhipster.light.generator.server.springboot.web.domain;

import static tech.jhipster.light.common.domain.FileUtils.getPath;
import static tech.jhipster.light.generator.project.domain.Constants.MAIN_JAVA;
import static tech.jhipster.light.generator.project.domain.Constants.TEST_JAVA;
import static tech.jhipster.light.generator.server.springboot.web.domain.SpringBootWeb.*;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jhipster.light.error.domain.UnauthorizedValueException;
import tech.jhipster.light.generator.buildtool.generic.domain.BuildToolService;
import tech.jhipster.light.generator.project.domain.Project;
import tech.jhipster.light.generator.project.domain.ProjectRepository;
import tech.jhipster.light.generator.server.springboot.properties.domain.SpringBootPropertiesService;

public class SpringBootWebDomainService implements SpringBootWebService {

  private final Logger log = LoggerFactory.getLogger(SpringBootWebDomainService.class);

  public static final String SOURCE = "server/springboot/web/";
  public static final String EXCEPTION_HANDLER_PATH = "technical/primary/exception";

  public final ProjectRepository projectRepository;
  public final BuildToolService buildToolService;
  public final SpringBootPropertiesService springBootPropertiesService;

  public SpringBootWebDomainService(
    ProjectRepository projectRepository,
    BuildToolService buildToolService,
    SpringBootPropertiesService springBootPropertiesService
  ) {
    this.projectRepository = projectRepository;
    this.buildToolService = buildToolService;
    this.springBootPropertiesService = springBootPropertiesService;
  }

  @Override
  public void init(Project project) {
    addSpringBootWeb(project);
  }

  @Override
  public void addSpringBootWeb(Project project) {
    buildToolService.addDependency(project, springBootStarterWebDependency());
    addSpringfoxDependencyAndProperty(project);

    addMvcPathmatchInProperties(project);
    addServerPortInProperties(project);
    addExceptionHandler(project);
  }

  @Override
  public void addSpringBootUndertow(Project project) {
    buildToolService.addDependency(project, springBootStarterWebDependency(), List.of(tomcatDependency()));
    buildToolService.addDependency(project, undertowDependency());
    addSpringfoxDependencyAndProperty(project);

    addMvcPathmatchInProperties(project);
    addServerPortInProperties(project);
    addExceptionHandler(project);
  }

  @Override
  public void addExceptionHandler(Project project) {
    buildToolService.addProperty(project, "problem-spring", SpringBootWeb.problemSpringVersion());
    buildToolService.addProperty(project, "problem-spring-web", "\\${problem-spring.version}");

    buildToolService.addDependency(project, problemSpringDependency());
    buildToolService.addDependency(project, springBootStarterValidation());

    String packageName = project.getPackageName().orElse("com.mycompany.myapp");
    springBootPropertiesService.addProperties(project, "application.exception.details", "false");
    springBootPropertiesService.addProperties(
      project,
      "application.exception.package",
      "org.,java.,net.,javax.,com.,io.,de.," + packageName
    );
    springBootPropertiesService.addPropertiesTest(project, "application.exception.package", "org.,java.");

    String packageNamePath = project.getPackageNamePath().orElse(getPath("com/mycompany/myapp"));
    templateToExceptionHandler(project, packageNamePath, "src", "BadRequestAlertException.java", MAIN_JAVA);
    templateToExceptionHandler(project, packageNamePath, "src", "ErrorConstants.java", MAIN_JAVA);
    templateToExceptionHandler(project, packageNamePath, "src", "ExceptionTranslator.java", MAIN_JAVA);
    templateToExceptionHandler(project, packageNamePath, "src", "FieldErrorDTO.java", MAIN_JAVA);
    templateToExceptionHandler(project, packageNamePath, "src", "HeaderUtil.java", MAIN_JAVA);
    templateToExceptionHandler(project, packageNamePath, "src", "ProblemConfiguration.java", MAIN_JAVA);

    templateToExceptionHandler(project, packageNamePath, "test", "BadRequestAlertExceptionTest.java", TEST_JAVA);
    templateToExceptionHandler(project, packageNamePath, "test", "ExceptionTranslatorIT.java", TEST_JAVA);
    templateToExceptionHandler(project, packageNamePath, "test", "ExceptionTranslatorTest.java", TEST_JAVA);
    templateToExceptionHandler(project, packageNamePath, "test", "ExceptionTranslatorTestController.java", TEST_JAVA);
    templateToExceptionHandler(project, packageNamePath, "test", "FieldErrorDTOTest.java", TEST_JAVA);
    templateToExceptionHandler(project, packageNamePath, "test", "HeaderUtilTest.java", TEST_JAVA);
  }

  private void templateToExceptionHandler(Project project, String source, String type, String sourceFilename, String destination) {
    projectRepository.template(project, getPath(SOURCE, type), sourceFilename, getPath(destination, source, EXCEPTION_HANDLER_PATH));
  }

  private void addSpringfoxDependencyAndProperty(Project project) {
    buildToolService.addDependency(project, springfoxDependency());
    buildToolService.addProperty(project, "springfox", springfoxVersion());
  }

  private void addMvcPathmatchInProperties(Project project) {
    springBootPropertiesService.addProperties(project, "spring.mvc.pathmatch.matching-strategy", "ant_path_matcher");
    springBootPropertiesService.addPropertiesTest(project, "spring.mvc.pathmatch.matching-strategy", "ant_path_matcher");
  }

  private void addServerPortInProperties(Project project) {
    springBootPropertiesService.addProperties(project, "server.port", getServerPort(project));
    springBootPropertiesService.addPropertiesTest(project, "server.port", 0);
  }

  private int getServerPort(Project project) {
    int serverPort;
    try {
      serverPort = project.getIntegerConfig("serverPort").orElse(8080);
    } catch (UnauthorizedValueException e) {
      log.warn("The serverPort config is not valid");
      serverPort = 8080;
    }
    return serverPort;
  }
}
