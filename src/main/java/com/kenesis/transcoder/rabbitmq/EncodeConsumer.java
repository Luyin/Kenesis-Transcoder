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

public class EncodeConsumer extends DefaultConsumer {
    Logger log = LoggerFactory.getLogger(EncodeConsumer.class);
    private JSONParser parser;
    Channel channel;
    EncodeService encodeService;
    FilesService filesService;

    public EncodeConsumer(Channel channel) {
        super(channel);
        // TODO Auto-generated constructor stub
        this.channel = channel;
        encodeService = EncodeService.getInstance();
        filesService = FilesService.getInstance();
        parser = new JSONParser();
    }

    @Override
    public void handleDelivery(String consumeTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException
    {
        // message handling logic here
        EncodeVO vo = new EncodeVO();
        String msg = new String(body,"UTF-8");
        JSONObject obj;

        UUID uuid = UUID.randomUUID();

        try {
            obj = (JSONObject) parser.parse(msg);
            String fileid = (String) obj.get("fileid");
            String abslocation = (String) obj.get("abslocation");
            // multiple - false if we are acknowledging multiple messages with the same delivery tag
            System.out.println(uuid+" S Channel :"+channel+" Thread:"+Thread.currentThread()+" msg:"+ obj.toJSONString());
            log.debug(uuid+" S Channel :"+channel+" Thread:"+Thread.currentThread()+" msg:"+ obj.toJSONString());

            vo.setFileid(Integer.parseInt(fileid));
            vo.setProgress(100);
            vo.setStatus("Started");
            encodeService.updateEncode(vo);

            transcode(abslocation,fileid);
            generate_mpd(fileid);
            generate_m3u8(fileid);

            vo.setStatus("Complete");
            vo.setProgress(100);
            encodeService.updateEncode(vo);

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void transcode(String InputPath, String fileid) throws IOException
    {
        /*  $HOME/kenesis/media/{fileid}/transcoded/media.mp4  */
        String HomePath = System.getProperty("user.home");
        StringBuilder outputPath = new StringBuilder();
        outputPath.append(HomePath);
        outputPath.append("/kenesis/media/");
        outputPath.append(fileid);

        createDirectory(outputPath.toString() + "/transcoded");

        ProcessBuilder pb = new ProcessBuilder(new String[]{"ffmpeg", "-threads", "4", "-i", InputPath, "-vcodec", "libx264", "-x264opts", "keyint=24:min-keyint=24:no-scenecut", "-b:v", "1000k", "-r", "24", "-f", "mp4", "-y", outputPath.toString() + "/transcoded/media.mp4"});

        System.out.println("Input Path : " + InputPath);
        System.out.println("Output Output : " + outputPath.toString());

        Process p = pb.start();

        EncodeVO vo = new EncodeVO();
        vo.setFileid(Integer.parseInt(fileid));
        vo.setProgress(0);
        vo.setStatus("Encoding");
        encodeService.updateEncode(vo);

        Scanner sc = new Scanner(p.getErrorStream());

        //Duration: 02:18:06.70, start: 0.000000, bitrate: 4887 kb/s
        // Find duration
        Pattern durPattern = Pattern.compile("(?<=Duration: )[^,]*");
        String dur = sc.findWithinHorizon(durPattern, 0);
        if (dur == null)
            throw new RuntimeException("Could not parse duration.");
        String[] dur_hms = dur.split(":");
        double totalSecs = Integer.parseInt(dur_hms[0]) * 3600
                + Integer.parseInt(dur_hms[1]) *   60
                + Double.parseDouble(dur_hms[2]);
        System.out.println("Total duration: " + dur);
        System.out.println("Total duration: " + totalSecs + " seconds.");

        //frame=  205 fps= 29 q=-1.0 Lsize= 628kB time=00:00:08.83 bitrate= 582.1kbits/s speed=1.25x
        // Find time as long as possible.
        Pattern timePattern = Pattern.compile("(?<=time=)[^\\f\\n\\r\\t\\vb]*");
        String match;
        while (null != (match = sc.findWithinHorizon(timePattern, 0))) {
            String[] prog_hms = match.split(":");
            double progress = (Double.parseDouble(prog_hms[0]) * 3600.0 + Double.parseDouble(prog_hms[1]) * 60.0 + Double.parseDouble(prog_hms[2])) / totalSecs;
            System.out.printf("Progress: %.2f%%%n", progress * 100);
            
            vo.setProgress((int)(progress*100));
            encodeService.updateEncode(vo);
        }
    }

    public void generate_mpd(String fileid) throws IOException
    {
        EncodeVO vo = new EncodeVO();
        vo.setFileid(Integer.parseInt(fileid));
        vo.setProgress(0);
        vo.setStatus("Segmenting");
        encodeService.updateEncode(vo);

        String HomePath = System.getProperty("user.home");
        StringBuilder MediaPath = new StringBuilder();
        MediaPath.append(HomePath);
        MediaPath.append("/kenesis/media/");
        MediaPath.append(fileid);

        createDirectory(MediaPath.toString() + "/segmented/mpd");

        ProcessBuilder pb = new ProcessBuilder(new String[]{
                "MP4Box",
                "-dash", "2000",
                "-profile", "dashavc264:live",
                "-bs-switching", "multi",
                "-url-template", MediaPath.toString() + "/transcoded/media.mp4#trackID=1:id=vid0:role=vid0", MediaPath.toString() + "/transcoded/media.mp4#trackID=2:id=aud0:role=aud0",
                "-out", MediaPath.toString() + "/segmented/mpd/media.mpd"});

        Process p = pb.start();

        Scanner sc = new Scanner(p.getErrorStream());

        while(sc.hasNext()){
            System.out.println(sc.nextLine());
        }

        vo.setProgress(100);
        vo.setStatus("Segmenting");
        encodeService.updateEncode(vo);
    }

    public void generate_m3u8(String fileid) throws IOException
    {
        String HomePath = System.getProperty("user.home");
        StringBuilder MediaPath = new StringBuilder();
        MediaPath.append(HomePath);
        MediaPath.append("/kenesis/media/");
        MediaPath.append(fileid);

        createDirectory(MediaPath.toString() + "/segmented/m3u8");

        ProcessBuilder pb = new ProcessBuilder(new String[]{
                "ffmpeg",
                "-threads", "4",
                "-i", MediaPath.toString() + "/transcoded/media.mp4",
                "-codec", "copy",
                "-vbsf", "h264_mp4toannexb",
                "-map", "0",
                "-f", "segment",
                "-segment_list", MediaPath.toString() +"/segmented/m3u8/media.m3u8",
                "-segment_time", "10",
                MediaPath.toString() + "/segmented/m3u8/media%03d.ts"});

        Process p = pb.start();

        Scanner sc = new Scanner(p.getErrorStream());

        while(sc.hasNext()){
            System.out.println(sc.nextLine());
        }
    }

    public void createDirectory(String path)
    {
        File theDir = new File(path);
        // if the directory does not exist, create it
        if (!theDir.exists()) {
            System.out.println("creating directory: " + theDir.getAbsolutePath());

            try{
                if(!theDir.mkdirs())
                {
                    System.out.println("Filed to create DIR");
                }
                else
                {
                    System.out.println("DIR created");
                }
            }
            catch(SecurityException se){

            }
        }
    }
}