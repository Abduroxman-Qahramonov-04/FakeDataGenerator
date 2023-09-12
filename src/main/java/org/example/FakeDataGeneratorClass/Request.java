package org.example.FakeDataGeneratorClass;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Request {
    private String fileName;
    private Integer count;
    private String type;
    private List<Pairs> pairs;
}
