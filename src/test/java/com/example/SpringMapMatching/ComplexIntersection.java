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
class ComplexIntersection {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper; // Autowire ObjectMapper to handle JSON parsing
    @Test
    void mapMatchingTest() throws Exception {
        List<Double[]> input = new ArrayList<>();
        input.add(new Double[] {76.69775605201723, 12.410476812445179});
        input.add(new Double[] {76.69801622629167, 12.410660179748557});
        input.add(new Double[] {76.69816911220552, 12.410827829741494});
        input.add(new Double[] {76.69843196868898, 12.410990240569193});
        input.add(new Double[] {76.6985821723938, 12.411333398597769});
        input.add(new Double[] {76.69886380434038, 12.411592731464362});
        input.add(new Double[] {76.69900327920915, 12.411920171583457});
        input.add(new Double[] {76.69918030500413, 12.412224035645565});
        input.add(new Double[] {76.69949144124986, 12.41255409448357});
        input.add(new Double[] {76.6996791958809, 12.412708645700372});
        input.add(new Double[] {76.69964700937273, 12.412923445544221});
        input.add(new Double[] {76.69975429773332, 12.413114669646339});
        input.add(new Double[] {76.70000106096269, 12.413318991134444});
        input.add(new Double[] {76.69999837875368, 12.41356784401232});
        input.add(new Double[] {76.70017808675767, 12.413769545644037});
        input.add(new Double[] {76.70018881559373, 12.414044593071774});
        input.add(new Double[] {76.70037925243379, 12.414217479877586});
        input.add(new Double[] {76.70051872730257, 12.414479429364569});
        input.add(new Double[] {76.70050799846649, 12.41470208622122});
        input.add(new Double[] {76.70072793960573, 12.414793768400955});
        input.add(new Double[] {76.7006528377533, 12.415173594230067});
        input.add(new Double[] {76.70081645250322, 12.415275754186075});
        input.add(new Double[] {76.70086473226549, 12.415587472778318});
        input.add(new Double[] {76.70102834701538, 12.41580227024646});
        input.add(new Double[] {76.7010819911957, 12.416106129776656});
        input.add(new Double[] {76.70125097036363, 12.416244962030122});
        input.add(new Double[] {76.7012643814087, 12.41652000684189});
        input.add(new Double[] {76.70140117406847, 12.416561918406748});
        input.add(new Double[] {76.70161306858064, 12.416994131025431});
        input.add(new Double[] {76.70164257287979, 12.417208927332554});
        input.add(new Double[] {76.7018035054207, 12.417384431500654});
        input.add(new Double[] {76.70202881097795, 12.417719722716605});
        input.add(new Double[] {76.7019832134247, 12.417913562753741});
        input.add(new Double[] {76.70207172632219, 12.418091685903804});
        input.add(new Double[] {76.70222461223602, 12.418267189476516});




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
        expected.add(new Double[] {76.69775999131707, 12.4103783167397});
        expected.add(new Double[] {76.69807200150898, 12.410650810929273});
        expected.add(new Double[] {76.69822152664301, 12.410819685496389});
        expected.add(new Double[] {76.6983710519735, 12.410988559953914});
        expected.add(new Double[] {76.69862071998806, 12.411300699756698});
        expected.add(new Double[] {76.69882100530882, 12.41158723050829});
        expected.add(new Double[] {76.69899694040569, 12.411861086162979});
        expected.add(new Double[] {76.69928518798542, 12.412222528327211});
        expected.add(new Double[] {76.69952549863635, 12.412544803004575});
        expected.add(new Double[] {76.6995922723601, 12.4126831350247});
        expected.add(new Double[] {76.69968338432045, 12.412871885732264});
        expected.add(new Double[] {76.69977449642639, 12.413060636389588});
        expected.add(new Double[] {76.69995672107508, 12.413438137553497});
        expected.add(new Double[] {76.70004783361784, 12.413626888060083});
        expected.add(new Double[] {76.70013894630621, 12.41381563851642});
        expected.add(new Double[] {76.7002300591402, 12.414004388922503});
        expected.add(new Double[] {76.7003211721198, 12.414193139278343});
        expected.add(new Double[] {76.70050339851588, 12.41457063983925});
        expected.add(new Double[] {76.70059451193237, 12.414759390044322});
        expected.add(new Double[] {76.70068562549449, 12.414948140199138});
        expected.add(new Double[] {76.7007246203805, 12.415179595361714});
        expected.add(new Double[] {76.70079874322714, 12.41535252841419});
        expected.add(new Double[] {76.70087286618441, 12.415525461431523});
        expected.add(new Double[] {76.70102111243082, 12.41587132736078});
        expected.add(new Double[] {76.70111428510607, 12.416054903426948});
        expected.add(new Double[] {76.70120745792507, 12.416238479441683});
        expected.add(new Double[] {76.70130063088783, 12.416422055404977});
        expected.add(new Double[] {76.70139380399435, 12.41660563131683});
        expected.add(new Double[] {76.70158315207853, 12.416985320553618});
        expected.add(new Double[] {76.70167932707177, 12.417181433874372});
        expected.add(new Double[] {76.7017755022244, 12.417377547139495});
        expected.add(new Double[] {76.70196708140845, 12.417744665350225});
        expected.add(new Double[] {76.70206248541574, 12.417915670299559});
        expected.add(new Double[] {76.70215788955826, 12.418086675196985});
        expected.add(new Double[] {76.70225329383602, 12.418257680042505});


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