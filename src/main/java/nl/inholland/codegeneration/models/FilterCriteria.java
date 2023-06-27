package nl.inholland.codegeneration.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class FilterCriteria {
    @Setter
    private String key;
    private String operation;
    @Setter
    private Object value;
}
