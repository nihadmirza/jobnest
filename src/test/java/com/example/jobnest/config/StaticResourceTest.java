package com.example.jobnest.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StaticResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void bootstrapCssShouldBeAccessible() throws Exception {
        // This tests if webjars-locator-core is working to resolve the version-less
        // path
        mockMvc.perform(get("/webjars/bootstrap/5.3.5/css/bootstrap.min.css"))
                .andExpect(status().isOk());
    }

    @Test
    void fontAwesomeCssShouldBeAccessible() throws Exception {
        mockMvc.perform(get("/css/font-awesome.min.css"))
                .andExpect(status().isOk());
    }

    @Test
    void themeCssShouldBeAccessible() throws Exception {
        mockMvc.perform(get("/css/theme.css"))
                .andExpect(status().isOk());
    }
}
