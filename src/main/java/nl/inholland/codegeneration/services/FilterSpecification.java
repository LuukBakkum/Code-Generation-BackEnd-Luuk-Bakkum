package nl.inholland.codegeneration.services;

import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import nl.inholland.codegeneration.models.FilterCriteria;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

// FilterSpecification object to add field filters based on the following article: https://www.baeldung.com/rest-api-search-language-spring-data-specifications
@AllArgsConstructor
public class FilterSpecification<T, J> implements Specification<T> {
    private FilterCriteria filterCriteria;
    private Join<T, J> join;

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Path<String> path;

        path = Objects.requireNonNullElse(join, root).get(filterCriteria.getKey());
//        System.out.println("Predicate path: ");
//        System.out.println(path.toString() + " : " + filterCriteria.getKey());
        if (filterCriteria.getOperation().equalsIgnoreCase(">:")) {
            return builder.greaterThanOrEqualTo(path, filterCriteria.getValue().toString());
        } else if (filterCriteria.getOperation().equalsIgnoreCase("<:")) {
            return builder.lessThanOrEqualTo(path, filterCriteria.getValue().toString());
        } else if (filterCriteria.getOperation().equalsIgnoreCase(">")) {
            return builder.greaterThan(path, filterCriteria.getValue().toString());
        } else if (filterCriteria.getOperation().equalsIgnoreCase("<")) {
            return builder.lessThan(path, filterCriteria.getValue().toString());
        } else if (filterCriteria.getOperation().equalsIgnoreCase(":")) {
            if (path.getJavaType() == String.class) {
                return builder.like(path, "%" + filterCriteria.getValue() + "%");
            } else {
                return builder.equal(path, filterCriteria.getValue());
            }
        }
        return null;
    }
}