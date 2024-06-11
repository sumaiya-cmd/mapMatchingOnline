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
        input.add(new Double[] {77.22875297069551, 12.71802067421573});
        input.add(new Double[] {77.22892999649049, 12.71835557344501});
        input.add(new Double[] {77.2292733192444, 12.718617213160481});
        input.add(new Double[] {77.2293484210968, 12.718993973877012});
        input.add(new Double[] {77.22962200641634, 12.719145724563207});
        input.add(new Double[] {77.22973465919496, 12.719611441619902});
        input.add(new Double[] {77.22997069358827, 12.719669002095692});
        input.add(new Double[] {77.23027646541597, 12.720061459537135});
        input.add(new Double[] {77.23029792308809, 12.72052194216141});
        input.add(new Double[] {77.230641245842, 12.72064229543687});
        input.add(new Double[] {77.23077535629274, 12.721071380563185});
        input.add(new Double[] {77.23100066184999, 12.721312086535955});
        input.add(new Double[] {77.23112404346467, 12.721662679609631});
        input.add(new Double[] {77.23131716251375, 12.721772566891246});
        input.add(new Double[] {77.2313815355301, 12.722091763010866});
        input.add(new Double[] {77.23149955272676, 12.722295839008101});
        input.add(new Double[] {77.23179459571838, 12.72263596530548});
        input.add(new Double[] {77.23200917243959, 12.722745852165671});
        input.add(new Double[] {77.23212718963623, 12.723169701037797});
        input.add(new Double[] {77.23245441913606, 12.723436568482976});
        input.add(new Double[] {77.23254024982454, 12.723797623991508});
        input.add(new Double[] {77.23283529281616, 12.72389704498336});
        input.add(new Double[] {77.23296940326692, 12.724451708749324});
        input.add(new Double[] {77.23312497138978, 12.72470287684828});
        input.add(new Double[] {77.23346829414369, 12.724859856783876});
        input.add(new Double[] {77.23367214202882, 12.725199979644474});
        input.add(new Double[] {77.23369359970094, 12.725513938803426});
        input.add(new Double[] {77.23397791385652, 12.725822664930874});
        input.add(new Double[] {77.2342085838318, 12.72596917889381});
        input.add(new Double[] {77.23447680473329, 12.726293602367766});
        input.add(new Double[] {77.23463773727418, 12.7266860495640});



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
            if (apiResult != null && apiResult.size() >= 15) {
                for (int i = actual.size(); i < apiResult.size(); i++) {
                    Double[] pointAsArray = new Double[]{apiResult.get(i).getLongitude(), apiResult.get(i).getLatitude()};
                    actual.add(pointAsArray);
                }
            }
        }

        List<Double[]> expected = new ArrayList<>() ;
        expected.add(new Double[] {77.22881570566172, 12.718063683273206});
        expected.add(new Double[] {77.22893891624236, 12.71824885619105});
        expected.add(new Double[] {77.22918533798001, 12.718619201758486});
        expected.add(new Double[] {77.22943176048622, 12.718989546968231});
        expected.add(new Double[] {77.22955348007946, 12.719175772342657});
        expected.add(new Double[] {77.22979691984037, 12.719548222827855});
        expected.add(new Double[] {77.22991864000807, 12.719734447938622});
        expected.add(new Double[] {77.23016208091796, 12.720106897896478});
        expected.add(new Double[] {77.23038661185569, 12.720440273449368});
        expected.add(new Double[] {77.23061114342259, 12.720773648706597});
        expected.add(new Double[] {77.23083567561866, 12.721107023668154});
        expected.add(new Double[] {77.23094794195265, 12.72127371103805});
        expected.add(new Double[] {77.23118185191767, 12.721654561696486});
        expected.add(new Double[] {77.23129880718554, 12.721844986900889});
        expected.add(new Double[] {77.23141576264365, 12.722035412022077});
        expected.add(new Double[] {77.231532718292, 12.722225837060044});
        expected.add(new Double[] {77.23187340050453, 12.722688089032498});
        expected.add(new Double[] {77.23199598785774, 12.722854420323213});
        expected.add(new Double[] {77.23224586856526, 12.723219323118421});
        expected.add(new Double[] {77.23237547498702, 12.723402151011658});
        expected.add(new Double[] {77.23263468842305, 12.723767806508086});
        expected.add(new Double[] {77.2327642954373, 12.723950634111267});
        expected.add(new Double[] {77.23308215652627, 12.724430640868562});
        expected.add(new Double[] {77.23318811050851, 12.724590642988385});
        expected.add(new Double[] {77.2334000189019, 12.724910647029105});
        expected.add(new Double[] {77.23360619179296, 12.725220989744741});
        expected.add(new Double[] {77.23381236522334, 12.7255313322096});
        expected.add(new Double[] {77.23405916815841, 12.725882942760968});
        expected.add(new Double[] {77.2341825698977, 12.726058747904657});
        expected.add(new Double[] {77.23441520745952, 12.726365745797187});
        expected.add(new Double[] {77.23463367909115, 12.726628131275893});

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