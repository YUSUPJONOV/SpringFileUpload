package uz.fastech.appuploaddownload.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity // jadval ochish uchun ishlatiladi va shu klassni jadval qilib beradi.
public class AttachmentContent {


    @Id // bu tableda primary key qilish vazifasini bajaradi
    @GeneratedValue(strategy = GenerationType.IDENTITY)// tableda sequence ni avtomatik amalga oshiradi. Misol uchun 1,2,3....
    private Integer id;

    private byte[] mainContent; // asosiy contentni saqlaydihgan o`zgaruvchi. ex. 1000Mb yoki shunga o`xshash razmerlarni

    @OneToOne
    private Attachment attachment;

}
