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
public class ClusteredPoints {

    @Autowired
    MockMvc mockMVC;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void observationsOnDifferentRoads() throws Exception{
        List<Double[]> input = new ArrayList<>();
        input.add(new Double[]{76.6670462656295, 12.382196756802529});
        input.add(new Double[]{76.66700427355835, 12.382501441649438});
        input.add(new Double[]{76.66692628828332, 12.382864719271879});
        input.add(new Double[]{76.6670642622314, 12.383028779967162});
        input.add(new Double[]{76.66675232113147, 12.38329244872645});
        input.add(new Double[]{76.66695628261988, 12.383485805647453});
        input.add(new Double[]{76.66676431886606, 12.383796348280924});
        input.add(new Double[]{76.66702227016026, 12.383954549103436});
        input.add(new Double[]{76.66668033472376, 12.384493603036631});
        input.add(new Double[]{76.66702826902753, 12.384587351432785});
        input.add(new Double[]{76.66668256307611, 12.384825403893728});
        input.add(new Double[]{76.66696450983949, 12.385048055997302});
        input.add(new Double[]{76.66663457213764, 12.385235552357514});
        input.add(new Double[]{76.6669825064414, 12.385487500380194});
        input.add(new Double[]{76.666622574403, 12.385645700177363});
        input.add(new Double[]{76.66694651323758, 12.385692574172694});
        input.add(new Double[]{76.66663457213764, 12.385921084780065});
        input.add(new Double[]{76.66670055967802, 12.386178891378805});
        input.add(new Double[]{76.6666645664742, 12.386395683094293});
        input.add(new Double[]{76.6666705653414, 12.386682785358929});
        input.add(new Double[]{76.66668856194337, 12.386946450425768});
        input.add(new Double[]{76.66679054268764, 12.386975746527739});



        List<Double[]> actual = new ArrayList<>();

        for(Double[] coord : input){
            String json = String.format("[%f, %f]", coord[0], coord[1]);

            MvcResult result = mockMVC.perform(
                            post("/processCoordinates")
                                    .content(json.getBytes())
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()).andReturn();

            String jsonResponse = result.getResponse().getContentAsString();
            List<Point> apiResult = objectMapper.readValue(jsonResponse, new TypeReference<>() {});
            if (apiResult != null) {
                for(int i = actual.size(); i < apiResult.size(); i++){
                    Double[] pointAsArray = new Double[]{apiResult.get(i).getLongitude(), apiResult.get(i).getLatitude()};
                    actual.add(pointAsArray);
                }
            }
        }

        List<Double[]> expected = new ArrayList<>();
        expected.add(new Double[]{76.66708422097928, 12.382257667020678});
        expected.add(new Double[]{76.66705428495258, 12.382454563316202});
        expected.add(new Double[]{76.66699441282184, 12.382848355906416});
        expected.add(new Double[]{76.66696447671778, 12.38304525220111});
        expected.add(new Double[]{76.66690560215164, 12.383407710086962});
        expected.add(new Double[]{76.6668467274875, 12.383770167971623});
        expected.add(new Double[]{76.66682517539428, 12.38394329505043});
        expected.add(new Double[]{76.66676383738434, 12.384508269789011});
        expected.add(new Double[]{76.66674560359503, 12.384726990369815});
        expected.add(new Double[]{76.66672736979932, 12.384945710949923});
        expected.add(new Double[]{76.66670913599717, 12.385164431529333});
        expected.add(new Double[]{76.66672215737488, 12.385572918194343});
        expected.add(new Double[]{76.66672866811048, 12.385777161520265});
        expected.add(new Double[]{76.66673517887725, 12.385981404841807});
        expected.add(new Double[]{76.66674168967518, 12.38618564815896});
        expected.add(new Double[]{76.66674820050429, 12.386389891471724});
        expected.add(new Double[]{76.66675471136455, 12.386594134780106});
        expected.add(new Double[]{76.66676684944129, 12.387034757392032});


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


