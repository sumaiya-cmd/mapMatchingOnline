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
public class SpiceGarden_WindowsWithIntersection {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper; // Autowire ObjectMapper to handle JSON parsing
    @Test
    void mapMatchingTest() throws Exception {
        List<Double[]> input = new ArrayList<>();
        input.add(new Double[] {77.70882546901704, 12.957415084653926});
        input.add(new Double[] {77.70879060029985, 12.95754839412512});
        input.add(new Double[] {77.7087476849556, 12.957660792251254});
        input.add(new Double[] {77.7087476849556, 12.95777580423479});
        input.add(new Double[] {77.70886838436128, 12.957807171130177});
        input.add(new Double[] {77.70891398191452, 12.957773190326662});
        input.add(new Double[] {77.7089622616768, 12.957812398945684});
        input.add(new Double[] {77.70902395248415, 12.957799329406697});
        input.add(new Double[] {77.70912587642671, 12.957862063187573});
        input.add(new Double[] {77.70919829607011, 12.957864677094772});
        input.add(new Double[] {77.70925462245943, 12.957916955232882});
        input.add(new Double[] {77.70933508872987, 12.957922183046097});
        input.add(new Double[] {77.70939946174623, 12.957974461172146});
        input.add(new Double[] {77.70947188138963, 12.957943094297827});
        input.add(new Double[] {77.70957112312318, 12.95799014460781});
        input.add(new Double[] {77.7095925807953, 12.9580371949089});
        input.add(new Double[] {77.70965963602067, 12.958021511476188});
        input.add(new Double[] {77.70964086055757, 12.958115612057632});
        input.add(new Double[] {77.70959794521333, 12.95816527623904});
        input.add(new Double[] {77.70960867404939, 12.958251535056917});
        input.add(new Double[] {77.70952820777893, 12.958301199211226});
        input.add(new Double[] {77.7094852924347, 12.958387457982017});
        input.add(new Double[] {77.70952552556993, 12.958413596997561});
        input.add(new Double[] {77.70943969488145, 12.958484172325855});



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
        expected.add(new Double[] {77.70884107000512, 12.95743000888136});
        expected.add(new Double[] {77.70882013651737, 12.957560811771714});
        expected.add(new Double[] {77.70880412855757, 12.9576424135401});
        expected.add(new Double[] {77.70878688921533, 12.957755215941347});
        expected.add(new Double[] {77.70884510595363, 12.957766268867829});
        expected.add(new Double[] {77.70895409708447, 12.957800532006985});
        expected.add(new Double[] {77.70895409708447, 12.957800532006985});
        expected.add(new Double[] {77.70905781445089, 12.957833081984859});
        expected.add(new Double[] {77.7091720793461, 12.957870771427594});
        expected.add(new Double[] {77.7091720793461, 12.957870771427594});
        expected.add(new Double[] {77.70927755463401, 12.95790846086571});
        expected.add(new Double[] {77.70929929779174, 12.957911630137929});
        expected.add(new Double[] {77.70939662353982, 12.957953585825067});
        expected.add(new Double[] {77.70945542953604, 12.95797364379797});
        expected.add(new Double[] {77.70957010122919, 12.958015670020416});
        expected.add(new Double[] {77.70961616592564, 12.95803381770456});
        expected.add(new Double[] {77.70964066842464, 12.95804336911705});
        expected.add(new Double[] {77.70962084861645, 12.958142610336239});
        expected.add(new Double[] {77.70962084861645, 12.958142610336239});
        expected.add(new Double[] {77.70958832987037, 12.958223738333743});
        expected.add(new Double[] {77.70956621712162, 12.958285851938925});
        expected.add(new Double[] {77.70950248037838, 12.958429093459216});
        expected.add(new Double[] {77.70950248037838, 12.958429093459216});
        expected.add(new Double[] {77.70946996163053, 12.95851782710308});
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
