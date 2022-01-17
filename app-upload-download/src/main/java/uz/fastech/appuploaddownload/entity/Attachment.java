package uz.fastech.appuploaddownload.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String fileOrginalName;// filening o`zining nomi

    private Long size;// filening o`lchami

    private String contentType;// uni turi misol uchun application/json, text/js shunga o`xshash turi

    // filesystem ga saqlagan ishlatiladi
    private String name; // papkani ichidan topish uchun


}
