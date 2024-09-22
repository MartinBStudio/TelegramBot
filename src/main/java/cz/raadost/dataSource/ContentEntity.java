package cz.raadost.dataSource;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CONTENT")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ContentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "content_seq")
    @SequenceGenerator(name = "content_seq", sequenceName = "content_seq", allocationSize = 1)
    private Long id;

    private int contentIndex;
    private String name;
    private String type;
    private String subType;
    private String description;
    private int price;
    private String previewUrl;
    private String fullUrl;

}
