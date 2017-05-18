package com.kenesis.transcoder.rabbitmq;

import com.kenesis.transcoder.domain.EncodeVO;
import com.kenesis.transcoder.service.EncodeService;
import com.kenesis.transcoder.service.FilesService;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;
import java.util.regex.Pattern;

public class DeleteConsumer extends DefaultConsumer {
    Logger log = LoggerFactory.getLogger(DeleteConsumer.class);
    private JSONParser parser;
    Channel channel;
    EncodeService encodeService;
    FilesService filesService;

    public DeleteConsumer(Channel channel) {
        super(channel);
        // TODO Auto-generated constructor stub
        this.channel = channel;
        filesService = FilesService.getInstance();
        encodeService = EncodeService.getInstance();
        parser = new JSONParser();
    }

    @Override
    public void handleDelivery(String consumeTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException
    {
        // message handling logic here
        String fileid = new String(body,"UTF-8");

        UUID uuid = UUID.randomUUID();

        try {
            // multiple - false if we are acknowledging multiple messages with the same delivery tag
            System.out.println(uuid+" S Channel :"+channel+" Thread:"+Thread.currentThread()+" msg:"+ fileid);
            log.debug(uuid+" S Channel :"+channel+" Thread:"+Thread.currentThread()+" msg:"+ fileid);

            //Delete from storage
            String HomePath = System.getProperty("user.home");
            StringBuilder outputPath = new StringBuilder();
            outputPath.append(HomePath);
            outputPath.append("/kenesis/media/");
            outputPath.append(fileid);
            outputPath.append("/");

            System.out.println("Removing...  " + outputPath.toString());
            deleteFile(outputPath.toString());

            //Delete from DB
            encodeService.cancleEncode(Integer.parseInt(fileid));
            filesService.deleteFile(Integer.parseInt(fileid));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean deleteFile(String delTarget) {
        File delDir = new File(delTarget);

        if(delDir.isDirectory()) {
            File[] allFiles = delDir.listFiles();

            for(File delAllDir : allFiles) {
                deleteFile(delAllDir.getAbsolutePath());
            }
        }

        return delDir.delete();
    }
}