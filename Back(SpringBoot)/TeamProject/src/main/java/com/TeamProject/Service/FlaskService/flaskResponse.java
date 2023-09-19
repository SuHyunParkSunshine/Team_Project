package com.TeamProject.Service.FlaskService;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.TeamProject.Domain.historyTable;
import com.TeamProject.Domain.threeOriginalPointCloud;
import com.TeamProject.Domain.twoSegmentationImage;
import com.TeamProject.Dto.poseDataDTO;
import com.TeamProject.Dto.threePointCloudCoordinatesDTO;
import com.TeamProject.Dto.twoSegmentationCoordinatesDTO;
import com.TeamProject.Dto.twoSegmentationImageDTO;
import com.TeamProject.Repository.threeOriginalPointCloudRepository;
import com.TeamProject.Service.SpringBootService.poseDataService;
import com.TeamProject.Service.SpringBootService.threePointCloudCoordinatesService;
import com.TeamProject.Service.SpringBootService.twoSegmentationCoordinatesService;
import com.TeamProject.Service.SpringBootService.twoSegmentationImageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class flaskResponse {

    
    // 2D_Segmentation
    private final twoSegmentationImageService twosegmentationimageservice;

    // 2D_coordinates
    private final twoSegmentationCoordinatesService twosegmentationcoordinatesservice;

    // 3D_PointCloud
    private final threeOriginalPointCloudRepository threeoriginalpointcloudrepository;

    // 3D_coordinates
    private final threePointCloudCoordinatesService threepointcloudcoordinatesservice;

    // 6D_pose
    private final poseDataService posedataservice ;
    

    // 이미지 파일의 기본 URL
	private final String imageBaseURL = "http://10.125.121.183:8080/upload/image/";

    // Json 데이터 파싱
    public String parsing(String json, historyTable history) {
        // json을 array로 변환
        JSONArray jsonArray = new JSONArray(json);

        // 파싱할 json 데이터 객체로 저장.
        JSONObject jsonObject = jsonArray.getJSONObject(0);

        // base64, 파일명 추출
        String imageValue = jsonObject.getString("image");
        String imageName = jsonObject.getString("image_name");
        //String pointCloudName = jsonObject.getString("ply_name");

        // 추출된 이미지 값 
        decoding(imageValue, imageName); // base64 Decoding
        twoSegmentationImage twosegmentationimage = SegDTO(imageName, history); // Segmentation 원본 저장
        threeOriginalPointCloud threeoriginalpointcloud = PointDTO(history);
        
        JSONArray detectionArray = jsonObject.getJSONArray("detections");

        for(int i = 0; i < detectionArray.length(); i++) {
            JSONObject detectionObject = detectionArray.getJSONObject(i);

            // 필요한 데이터 추출 및 처리
            //int objectNum = detectionObject.getInt("Object_num");
            double accuracy = detectionObject.getDouble("accuracy"); //정확도
          
            int className = (int) detectionObject.getDouble("class_name"); // 탐지객체 
            String classNameString = "";
            switch(className) {
                case 0 : classNameString = "bongji";
                    break;
                case 1 : classNameString = "box";
                    break;
                case 2 : classNameString = "canSnack";
                    break;
                case 3 : classNameString = "energdrink";
                    break;
                case 4 : classNameString = "milk";
                    break;
            }
       
            JSONArray xCoordinates = detectionObject.getJSONArray("x_coordinates");
            JSONArray yCoordinates = detectionObject.getJSONArray("y_coordinates");
            JSONArray xPoint = detectionObject.getJSONArray("x_point"); // 3pd
            JSONArray yPoint = detectionObject.getJSONArray("y_point"); // 3pd
            JSONArray zPoint = detectionObject.getJSONArray("z_point"); // 3pd
            JSONArray boxInfo = detectionObject.getJSONArray("box_info"); // segData
            JSONArray sixPose = detectionObject.getJSONArray("6dpose"); // poseData

            double xBox = boxInfo.optDouble(0); // 배열값 저장
            double yBox = boxInfo.optDouble(1);
            double width = boxInfo.optDouble(2);
            double height = boxInfo.optDouble(3);
            twoCoorDTO(accuracy, classNameString, xCoordinates.toString(), yCoordinates.toString(), xBox, yBox, width, height, twosegmentationimage); // Segmentation수치 저장.
            threeCoorDTO(classNameString, xPoint.toString(), yPoint.toString(), zPoint.toString(), threeoriginalpointcloud); // PointCloud 수치 저장

            double centerX = sixPose.optDouble(0);
            double centerY = sixPose.optDouble(1);
            double centerZ = sixPose.optDouble(2);
            double rx = sixPose.optDouble(3);
            double ry = sixPose.optDouble(4);
            double rz = sixPose.optDouble(5);
            sixPoseDTO(classNameString, centerX, centerY, centerZ, rx, ry, rz, history);
        }
        return imageName;
    }

    // Base64 디코딩
    public void decoding(String imageValue, String imageName) {

        byte[] imageData = Base64.getDecoder().decode(imageValue);

        // 이미지 파일 저장 및 경로
        String imagePath = "C:/Team_Project/Back(SpringBoot)/TeamProject/image/Segmentation/" + imageName;

        // 이미지 저장
        try (FileOutputStream fos = new FileOutputStream(imagePath)) {
            fos.write(imageData);
            System.out.println("이미지 저장 완료: " + imagePath);
        } catch (IOException e) {
            System.out.println("이미지 저장 실패: " + e.getMessage());
        }
    }

    // 2D_Segmentation
    public twoSegmentationImage SegDTO(String imageName, historyTable history) {
        twoSegmentationImageDTO twosegmentationimagedto = new twoSegmentationImageDTO();

        // DTO 통해서 전달.
        twosegmentationimagedto.setTwoSegmentationPath(imageBaseURL + imageName);
        twosegmentationimagedto.setHistoryId(history);
        return twosegmentationimageservice.segmentationImage(twosegmentationimagedto);
    }

    // 2D_coordinates
    public void twoCoorDTO(double accuracy, String classNameString, String xCoordinates, String yCorrdinates, double xBox, double yBox, double width, double height, twoSegmentationImage twosegmentationimage) {
        twoSegmentationCoordinatesDTO twosegmentationcorrdinatesdto = new twoSegmentationCoordinatesDTO();

        // DTO 통해서 전달
        twosegmentationcorrdinatesdto.setTwoObjectAcc(accuracy);
        twosegmentationcorrdinatesdto.setTwoObjectId(classNameString);
        twosegmentationcorrdinatesdto.setXList(xCoordinates);
        twosegmentationcorrdinatesdto.setYList(yCorrdinates);
        twosegmentationcorrdinatesdto.setXBox(xBox);
        twosegmentationcorrdinatesdto.setYBox(yBox);
        twosegmentationcorrdinatesdto.setWidth(width);
        twosegmentationcorrdinatesdto.setHeight(height);
        twosegmentationcorrdinatesdto.setTwoSegmentationId(twosegmentationimage);
        twosegmentationcoordinatesservice.twoCoordinates(twosegmentationcorrdinatesdto);        
    }

    // 3D_PointCloud
    public threeOriginalPointCloud PointDTO(historyTable history) {
        return threeoriginalpointcloudrepository.findByHistoryId(history);
    }

    // 3D_coordinates
    public void threeCoorDTO(String classNameString, String xPoint, String yPoint, String zPoint, threeOriginalPointCloud threeoriginalpointcloud) {
        threePointCloudCoordinatesDTO threepointcloudcoordinatesdto = new threePointCloudCoordinatesDTO();

        // DTO 통해서 전달
        threepointcloudcoordinatesdto.setThreeObjectId(classNameString);
        threepointcloudcoordinatesdto.setXList(xPoint);
        threepointcloudcoordinatesdto.setYList(yPoint);
        threepointcloudcoordinatesdto.setZList(zPoint);
        threepointcloudcoordinatesdto.setThreeOriginalId(threeoriginalpointcloud);
        threepointcloudcoordinatesservice.threeCoordinates(threepointcloudcoordinatesdto);
    }

    // // 6D
    public void sixPoseDTO(String classNameString, double centerX, double centerY, double centerZ, double rx, double ry, double rz, historyTable history) {
        poseDataDTO posedatadto = new poseDataDTO();

        // DTO 통해서 전달
        posedatadto.setObjectId(classNameString);
        posedatadto.setX(centerX);
        posedatadto.setY(centerY);
        posedatadto.setZ(centerZ);
        posedatadto.setRx(rx);
        posedatadto.setRy(ry);
        posedatadto.setRz(rz);
        posedataservice.poseDataUpload(posedatadto, history);
    }
}   




























