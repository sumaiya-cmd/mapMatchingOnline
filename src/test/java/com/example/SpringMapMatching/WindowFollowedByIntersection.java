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
class WindowFollowedByIntersection {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper; // Autowire ObjectMapper to handle JSON parsing
    @Test
    void mapMatchingTest() throws Exception {
        List<Double[]> input = new ArrayList<>();
        input.add(new Double[] {76.69613331556322, 12.409177520424896});
        input.add(new Double[] {76.6963291168213, 12.409203715892563});
        input.add(new Double[] {76.69648200273515, 12.409368747278306});
        input.add(new Double[] {76.6966536641121, 12.409379225457998});
        input.add(new Double[] {76.69680118560791, 12.409549495818872});
        input.add(new Double[] {76.69705867767335, 12.409588788963246});
        input.add(new Double[] {76.69713646173479, 12.409947666074304});
        input.add(new Double[] {76.69740468263628, 12.409890036206596});
        input.add(new Double[] {76.69764876365663, 12.410023632698602});
        input.add(new Double[] {76.6978043317795, 12.410301303619578});
        input.add(new Double[] {76.69826030731203, 12.410419182694591});
        input.add(new Double[] {76.69832199811935, 12.410819971150476});
        input.add(new Double[] {76.69859290122987, 12.411032153024399});
        input.add(new Double[] {76.6987779736519, 12.411294105716681});
        input.add(new Double[] {76.6989576816559, 12.411535101960776});
        input.add(new Double[] {76.69892013072969, 12.411878259271196});
        input.add(new Double[] {76.69907033443451, 12.412059006069633});
        input.add(new Double[] {76.69921517372133, 12.412208318547597});
        input.add(new Double[] {76.69952094554903, 12.412127113526342});
        input.add(new Double[] {76.69950485229494, 12.412582909124176});
        input.add(new Double[] {76.69966310262681, 12.412692928631664});
        input.add(new Double[] {76.69963359832764, 12.412868435845004});
        input.add(new Double[] {76.69969797134401, 12.412988933266242});
        input.add(new Double[] {76.6998401284218, 12.413054420971786});
        input.add(new Double[] {76.69988840818407, 12.413151342745753});
        input.add(new Double[] {76.69983476400377, 12.41327184003604});
        input.add(new Double[] {76.69986963272096, 12.413374000738449});
        input.add(new Double[] {76.69993668794633, 12.413491878422198});
        input.add(new Double[] {76.70008420944215, 12.413539029480742});
        input.add(new Double[] {76.70020490884781, 12.41372763362954});
        input.add(new Double[] {76.70013785362245, 12.413913618142436});
        input.add(new Double[] {76.70017808675767, 12.414031495581806});
        input.add(new Double[] {76.70025855302812, 12.414159850955075});
        input.add(new Double[] {76.70033097267151, 12.41426725030041});
        input.add(new Double[] {76.70044362545015, 12.414296064751367});
        input.add(new Double[] {76.7006340622902, 12.414382508085115});
        input.add(new Double[] {76.70048117637636, 12.41459468705532});
        input.add(new Double[] {76.7005616426468, 12.414788529420125});
        input.add(new Double[] {76.7008674144745, 12.414804246362285});




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
                apiResult.clear();
            }
        }

        List<Double[]> expected = new ArrayList<>() ;
        expected.add(new Double[] {76.69612065074257, 12.40913707382197});
        expected.add(new Double[] {76.69629197200915, 12.409233033819836});
        expected.add(new Double[] {76.69646329339149, 12.409328993692442});
        expected.add(new Double[] {76.69663461488953, 12.409424953439789});
        expected.add(new Double[] {76.69680593650334, 12.409520913061876});
        expected.add(new Double[] {76.69697725823286, 12.409616872558699});
        expected.add(new Double[] {76.69729971641681, 12.40985345835999});
        expected.add(new Double[] {76.69746094571873, 12.409971751086879});
        expected.add(new Double[] {76.69762217516063, 12.41009004369793});
        expected.add(new Double[] {76.6977834047425, 12.410208336193136});
        expected.add(new Double[] {76.69807087113254, 12.410511385695555});
        expected.add(new Double[] {76.69835833819423, 12.410814434800045});
        expected.add(new Double[] {76.69861323940674, 12.41113745880639});
        expected.add(new Double[] {76.69872440699116, 12.411308958343103});
        expected.add(new Double[] {76.69894674262389, 12.411651957216671});
        expected.add(new Double[] {76.69905791067221, 12.411823456553522});
        expected.add(new Double[] {76.69916907887519, 12.411994955823749});
        expected.add(new Double[] {76.69928024723279, 12.412166455027347});
        expected.add(new Double[] {76.69928024723279, 12.412166455027347});
        expected.add(new Double[] {76.69939141574504, 12.412337954164316});
        expected.add(new Double[] {76.6995922723601, 12.4126831350247});
        expected.add(new Double[] {76.69968338432045, 12.412871885732264});
        expected.add(new Double[] {76.69977449642639, 12.413060636389588});
        expected.add(new Double[] {76.69977449642639, 12.413060636389588});
        expected.add(new Double[] {76.69986560867792, 12.413249386996664});
        expected.add(new Double[] {76.69986560867792, 12.413249386996664});
        expected.add(new Double[] {76.69995672107508, 12.413438137553497});
        expected.add(new Double[] {76.69995672107508, 12.413438137553497});
        expected.add(new Double[] {76.70004783361784, 12.413626888060083});
        expected.add(new Double[] {76.70013894630621, 12.41381563851642});
        expected.add(new Double[] {76.70013894630621, 12.41381563851642});
        expected.add(new Double[] {76.7002300591402, 12.414004388922503});
        expected.add(new Double[] {76.7003211721198, 12.414193139278343});
        expected.add(new Double[] {76.7003211721198, 12.414193139278343});
        expected.add(new Double[] {76.70041228524504, 12.414381889583924});
        expected.add(new Double[] {76.70041228524504, 12.414381889583924});
        expected.add(new Double[] {76.70050339851588, 12.41457063983925});
        expected.add(new Double[] {76.70059451193237, 12.414759390044322});
        expected.add(new Double[] {76.70068562549449, 12.414948140199138});


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

