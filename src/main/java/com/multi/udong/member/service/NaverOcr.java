package com.multi.udong.member.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

/**
 * The type Naver ocr.
 *
 * @author : 재식
 * @since : 24. 7. 23.
 */
@Service
public class NaverOcr {

    @Value("${naver.ocr.url}")
    private String OCR_URL;

    @Value("${naver.ocr.secret}")
    private String OCR_SECRET;

    /**
     * Ocr array list.
     *
     * @param fileName the file name
     * @return the array list
     * @since 2024 -08-01
     */
    public ArrayList<String> ocr(String fileName) {
        String apiURL = OCR_URL;
        String secretKey = OCR_SECRET;
        String imageFile = fileName;

        ArrayList<String> list = new ArrayList<String>();
        try {
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setUseCaches(false);
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setReadTimeout(30000);
            con.setRequestMethod("POST");
            String boundary = "----" + UUID.randomUUID().toString().replaceAll("-", "");
            con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            con.setRequestProperty("X-OCR-SECRET", secretKey);


            String orgname = fileName.substring(imageFile.lastIndexOf("\\")+1);
            String ext = orgname.substring(orgname.lastIndexOf(".")+1);
            String fname = orgname.substring(0, orgname.lastIndexOf("."));


            JSONObject json = new JSONObject();
            json.put("version", "V2");
            json.put("requestId", UUID.randomUUID().toString());
            json.put("timestamp", System.currentTimeMillis());

            JSONObject image = new JSONObject();
            image.put("format", ext); // 확장자
            image.put("name", fname);

            JSONArray images = new JSONArray();
            images.put(image);
            json.put("images", images);

            String postParams = json.toString();
            System.out.println(images);
            System.out.println("json  -----  >" +json);
            con.connect();
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            long start = System.currentTimeMillis();
            File file = new File(imageFile);
            writeMultiPart(wr, postParams, file, boundary);
            wr.close();

            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();

            System.out.println("res "+response);
            //String을 json으로 만들어서 inferText만 추출해봅시다.
            JSONObject json2 = new JSONObject(response.toString());
            System.out.println(json2);
            JSONArray images_arr = json2.getJSONArray("images");
            //키에 대한 값이 array이면 getJSONArray("key");
            //키에 대한 값이 json이면 getJSONObject("key");
            //키에 대한 값이 String이면 getString("key");
            JSONObject images_0 = images_arr.getJSONObject(0);
            JSONArray fields_arr = images_0.getJSONArray("fields");
            for (int i = 0; i < fields_arr.length(); i++) {
                JSONObject inferText = fields_arr.getJSONObject(i);
                String text = inferText.getString("inferText");
                System.out.println(text);
                list.add(text);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return list;
    }

    private static void writeMultiPart(OutputStream out, String jsonMessage, File file, String boundary) throws
            IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("--").append(boundary).append("\r\n");
        sb.append("Content-Disposition:form-data; name=\"message\"\r\n\r\n");
        sb.append(jsonMessage);
        sb.append("\r\n");

        out.write(sb.toString().getBytes("UTF-8"));
        out.flush();

        if (file != null && file.isFile()) {
            out.write(("--" + boundary + "\r\n").getBytes("UTF-8"));
            StringBuilder fileString = new StringBuilder();
            fileString
                    .append("Content-Disposition:form-data; name=\"file\"; filename=");
            fileString.append("\"" + file.getName() + "\"\r\n");
            fileString.append("Content-Type: application/octet-stream\r\n\r\n");
            out.write(fileString.toString().getBytes("UTF-8"));
            out.flush();

            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[8192];
                int count;
                while ((count = fis.read(buffer)) != -1) {
                    out.write(buffer, 0, count);
                }
                out.write("\r\n".getBytes());
            }

            out.write(("--" + boundary + "--\r\n").getBytes("UTF-8"));
        }
        out.flush();
    }
}
//res {"code":"1021","message":"Not Found Deploy Info: Please confirm the template is released.","path":"/external/v1/31853/8c2c8f8be327e59d5e0261d6fc8d7fadf5c2e85d31657e897e5149913eab86c0/infer","traceId":"428fad6f1e0f401193a14c772e379b82","timestamp":1718923585496}

//에러뜨면 서비스 배포 꼭해야한다   템플릿 빌더 들어가서 오른쪽 위 서비스 배포 클릭