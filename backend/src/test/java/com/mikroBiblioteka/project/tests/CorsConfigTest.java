import static org.assertj.core.api.Assertions.assertThat;

import com.mikroBiblioteka.project.config.CorsConfig;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

class CorsConfigTest {

    @Test
    void testCorsConfigurerBean() {
        CorsConfig config = new CorsConfig();
        WebMvcConfigurer webMvcConfigurer = config.corsConfigurer();
        assertThat(webMvcConfigurer).isNotNull();

        CorsRegistry registry = new CorsRegistry();
        webMvcConfigurer.addCorsMappings(registry);
    }
}
