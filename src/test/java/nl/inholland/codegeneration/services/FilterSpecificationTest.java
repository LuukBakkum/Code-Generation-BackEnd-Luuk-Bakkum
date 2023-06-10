package nl.inholland.codegeneration.services;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import nl.inholland.codegeneration.models.FilterCriteria;
import nl.inholland.codegeneration.services.FilterSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FilterSpecificationTest {

    @Mock
    private Root<Object> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder builder;

    private FilterCriteria filterCriteria;
  
    private FilterSpecification<Object, Object> filterSpecification;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock the behavior of root.get() method
        Path<Object> path = mock(Path.class);
        when(root.get(anyString())).thenReturn(path);
        when(Path.class.cast(path).getJavaType()).thenReturn(String.class);

        // Mock the behavior of builder.like() and builder.equal() methods
        Predicate predicate = Mockito.mock(Predicate.class);
        when(builder.like(any(Path.class), anyString())).thenReturn(predicate);

    }

    @Test
    public void testToPredicateWithEqualsOperation() {
        filterCriteria = new FilterCriteria("key", ":", "value");
        filterSpecification = new FilterSpecification<>(filterCriteria, null);
        Predicate result = filterSpecification.toPredicate(root, query, builder);
        assert result != null;
    }
}