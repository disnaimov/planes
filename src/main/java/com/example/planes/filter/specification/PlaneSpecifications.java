package com.example.planes.filter.specification;

import com.example.planes.enums.PlaneStatus;
import com.example.planes.enums.PlaneType;
import com.example.planes.model.Plane;
import org.springframework.data.jpa.domain.Specification;

public class PlaneSpecifications {

    public static Specification<Plane> filterByCapacity(Integer capacity) {
        return ((root, query, criteriaBuilder) -> {
            if (capacity == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("capacity"), capacity);
        });
    }

    public static Specification<Plane> filterByType(String type) {
        return ((root, query, criteriaBuilder) -> {
            if (type == null) {
                return criteriaBuilder.conjunction();
            }
            PlaneType planeType = PlaneType.fromString(type);
            return criteriaBuilder.equal(root.get("type"), planeType);
        });
    }

    public static Specification<Plane> filterByStatus(String status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            PlaneStatus planeStatus = PlaneStatus.fromString(status);
            return criteriaBuilder.equal(root.get("status"), planeStatus);
        };
    }
}
