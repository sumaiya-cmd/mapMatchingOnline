package com.example.SpringMapMatching;
import com.example.SpringMapMatching.Model.Point;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.util.ArrayList;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
class SpiceGarden_IntersectionDetection {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper; // Autowire ObjectMapper to handle JSON parsing
    @Test
    void mapMatchingTest() throws Exception {
        List<Double[]> input = new ArrayList<>();
        input.add(new Double[] {77.70896762609483, 12.956583859282604});
        input.add(new Double[] {77.70894885063173, 12.956758991902872});
        input.add(new Double[] {77.70889520645143, 12.956939352233801});
        input.add(new Double[] {77.70888179540636, 12.957169376814152});
        input.add(new Double[] {77.70878523588182, 12.957402015094088});
        input.add(new Double[] {77.70887374877931, 12.95760328623956});
        input.add(new Double[] {77.7087825536728, 12.95768693134308});
        input.add(new Double[] {77.70872622728348, 12.957848993651208});
        input.add(new Double[] {77.70879596471788, 12.957971847266093});
        input.add(new Double[] {77.7086725831032, 12.9580842452011});
        input.add(new Double[] {77.70866453647614, 12.958254148960032});
        input.add(new Double[] {77.70871818065645, 12.958377002375025});
        input.add(new Double[] {77.70861089229585, 12.95850769743083});


        List<Double[]> actual = new ArrayList<>(); // List to store converted points
        for (Double[] coord : input) {
            String json = String.format("[%f, %f]", coord[0], coord[1]);
            MvcResult mvcResult = mockMvc.perform(
                            post("/processCoordinates")
                                    .content(json.getBytes())
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()).andReturn();
            String jsonResponse = mvcResult.getResponse().getContentAsString();
            System.out.println(jsonResponse);
            // Parse the response to List<Point>
            List<Point> apiResult = objectMapper.readValue(jsonResponse, new TypeReference<>() {});
            if (apiResult != null) {
                for (int i = actual.size(); i < apiResult.size(); i++) {
                    Double[] pointAsArray = new Double[]{apiResult.get(i).getLongitude(), apiResult.get(i).getLatitude()};
                    actual.add(pointAsArray);
                }
            }
        }

        List<Double[]> expected = new ArrayList<>() ;
        expected.add(new Double[] {77.70902331449633, 12.956591188827431});
        expected.add(new Double[] {77.70899129857514, 12.956744792731996});
        expected.add(new Double[] {77.70893342364138, 12.956983598614286});
        expected.add(new Double[] {77.70889648219134, 12.9571540026201});
        expected.add(new Double[] {77.70884107000512, 12.95743000888136});
        expected.add(new Double[] {77.70882013651737, 12.957560811771714});
        expected.add(new Double[] {77.7087905833593, 12.957726415333326});
        expected.add(new Double[] {77.70878688921533, 12.957755215941347});
        expected.add(new Double[] {77.70875273736067, 12.957919237712886});
        expected.add(new Double[] {77.7087182586763, 12.958090841154458});
        expected.add(new Double[] {77.70867639202999, 12.958270844954129});
        expected.add(new Double[] {77.70866161545092, 12.958352446489855});
        expected.add(new Double[] {77.70862959952967, 12.95852404963206});


        Assertions.assertEquals(convert(expected) , convert(actual));
    }
    List<List<Double>> convert(List<Double[]> coords){
        List<List<Double>> resultList = new ArrayList<>();
        for (Double[] array : coords) {
            List<Double> list = new ArrayList<>();
            for (Double value : array) {
                list.add(value);
            }
            resultList.add(list);
        }
        return resultList;
    }
}