package com.fx.pan.factory.preview;

import com.fx.pan.factory.domain.ThumbImage;
import com.fx.pan.factory.fxUtils;
import com.fx.pan.factory.operation.ImageOperation;
import com.fx.pan.factory.operation.VideoOperation;
import com.fx.pan.factory.preview.domain.PreviewFile;
import com.fx.pan.factory.utils.CharsetUtils;
import com.fx.pan.utils.FileUtils;
import lombok.Data;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @Author leaving
 * @Date 2022/3/10 14:52
 * @Version 1.0
 */

@Data
public abstract class Previewer {

    private ThumbImage thumbImage;

    public void imageThumbnailPreview(HttpServletResponse httpServletResponse, PreviewFile previewFile) {
        String fileUrl = previewFile.getFileUrl();
        boolean isVideo = fxUtils.isVideoFile(FileUtils.getFileExtendName(fileUrl));
        String thumbnailImgUrl = previewFile.getFileUrl();
        if (isVideo) {
            thumbnailImgUrl = fileUrl.replace("." + FileUtils.getFileExtendName(fileUrl), ".jpg");
        }


        File saveFile = fxUtils.getCacheFile(thumbnailImgUrl);

        if (saveFile.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(saveFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            com.fx.pan.factory.utils.IOUtils.writeInputStreamToResponse(fis, httpServletResponse);

        } else {
            InputStream inputstream = getInputStream(previewFile.getFileUrl());
            InputStream in = null;
            try {
                int thumbImageWidth = thumbImage.getWidth();
                int thumbImageHeight = thumbImage.getHeight();
                int width = thumbImageWidth == 0 ? 150 : thumbImageWidth;
                int height = thumbImageHeight == 0 ? 150 : thumbImageHeight;

                if (isVideo) {
                    // in = VideoOperation.thumbnailsImage(inputstream, saveFile, width, height);
                } else {
                    in = ImageOperation.thumbnailsImage(inputstream, saveFile, width, height);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            com.fx.pan.factory.utils.IOUtils.writeInputStreamToResponse(in, httpServletResponse);

        }
    }

    public void imageOriginalPreview(HttpServletResponse httpServletResponse, PreviewFile previewFile) {

        InputStream inputStream = getInputStream(previewFile.getFileUrl());

        OutputStream outputStream = null;

        try {
            outputStream = httpServletResponse.getOutputStream();
            byte[] bytes = IOUtils.toByteArray(inputStream);
            bytes = CharsetUtils.convertCharset(bytes, FileUtils.getFileExtendName(previewFile.getFileUrl()));
            outputStream.write(bytes);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    protected abstract InputStream getInputStream(String fileUrl);
}
