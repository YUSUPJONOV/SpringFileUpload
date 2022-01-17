package uz.fastech.appuploaddownload.controller;

import javassist.NotFoundException;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import uz.fastech.appuploaddownload.entity.Attachment;
import uz.fastech.appuploaddownload.entity.AttachmentContent;
import uz.fastech.appuploaddownload.repository.AttachmentContentRepository;
import uz.fastech.appuploaddownload.repository.AttachmentRepository;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/attachment")
public class AttachmentController {
    @Autowired
    AttachmentRepository attachmentRepository;
    @Autowired
    AttachmentContentRepository attachmentContentRepository;

    private static final String uploadDirectory = "D:\\spring-boot-OTP-app\\app-upload-download\\src\\FileSystemUpload"; // filesystemga yuklashda ochilgan file


    @SneakyThrows
    @PostMapping("/uploadDb")
    public String uploadFile(MultipartHttpServletRequest request) { // Bu request serverdan malumotlarni olish uchun ishlatiladi
        // sababi hech qanday malumot birdaniga kelmaganligi uchun u sekin qism qism bolib kelganligi uchun Multi request dan foydalaniladi


        Iterator<String> fileNames = request.getFileNames();// serverga birdaniga ko`p malumotarni uzatgani uchun
        // biz requestning getFileNames() methodidan foydalanamiz.

        MultipartFile file = request.getFile(fileNames.next());

        if (file != null) {
            // file haqida malumot olish uchun
            String originalFilename = file.getOriginalFilename();
            long size = file.getSize();
            String contentType = file.getContentType();

            Attachment attachment = new Attachment();
            attachment.setFileOrginalName(originalFilename);
            attachment.setSize(size);
            attachment.setContentType(contentType);

            Attachment save = attachmentRepository.save(attachment);

            // file contentni (byte[]) korinishida saqlaymiz
            AttachmentContent attachmentContent = new AttachmentContent();
            attachmentContent.setMainContent(file.getBytes());
            attachmentContent.setAttachment(save);
            attachmentContentRepository.save(attachmentContent);
            return "File id: " + save.getId();
        }

        return "The error has been occured";
    }

    @SneakyThrows
    @GetMapping("/getFile/{id}")
    public void getFile(@PathVariable Integer id, HttpServletResponse response) {

        Optional<Attachment> optionalAttachment = attachmentRepository.findById(id);
        if (optionalAttachment.isPresent()) {
            Attachment attachment = optionalAttachment.get();
            Optional<AttachmentContent> contentOptional = attachmentContentRepository.findByAttachmentId(id);
            if (contentOptional.isPresent()) {
                AttachmentContent attachmentContent = contentOptional.get();

                response.setHeader("Content-Disposition", "attachment; filename=\"" + attachment.getFileOrginalName() + "\"");
                response.setContentType(attachment.getContentType());

                FileCopyUtils.copy(attachmentContent.getMainContent(), response.getOutputStream());

            }
        }
    }

    /**
     * FileSystemga yuklash shu yerdan boshlanadi
     */

    @PostMapping("uploadSystem")
    public String uploadFileToFileSystem(MultipartHttpServletRequest request) throws IOException {
        Iterator<String> fileNames = request.getFileNames();
        MultipartFile file = request.getFile(fileNames.next());
        if (file != null) {
            String orginalFileName = file.getOriginalFilename();
            Attachment attachment = new Attachment();
            attachment.setFileOrginalName(orginalFileName);
            attachment.setSize(file.getSize());
            attachment.setContentType(file.getContentType());

            String[] split = orginalFileName.split("\\.");

            String name = UUID.randomUUID().toString() + "." + split[split.length - 1];

            attachment.setName(name);
            attachmentRepository.save(attachment);
            Path path = Paths.get(uploadDirectory + "/" + name);
            Files.copy(file.getInputStream(), path);
            return "File saqlandi. ID si " + attachment.getId();
        }
        return "saqlanmadi";

    }

    /**
     * FileSystemdan saqlangan malumotni o`qish
     */

    @GetMapping("/getFileFromSystem/{id}")
    public void getFileFromSystem(@PathVariable Integer id, HttpServletResponse response) throws IOException {
        Optional<Attachment> optionalAttachment = attachmentRepository.findById(id);
        if (optionalAttachment.isPresent()) {
            Attachment attachment = optionalAttachment.get();
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + attachment.getFileOrginalName() + "\"");

            response.setContentType(attachment.getContentType());
            FileInputStream fileInputStream= new FileInputStream(uploadDirectory+"/"+attachment.getName());

            FileCopyUtils.copy(fileInputStream,response.getOutputStream());
        }
    }
}
