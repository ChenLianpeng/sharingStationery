package person.clp.stationeryback.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import person.clp.stationeryback.entity.Record;
import person.clp.stationeryback.entity.Stationery;
import person.clp.stationeryback.entity.User;

import person.clp.stationeryback.service.RecordService;
import person.clp.stationeryback.service.StationeryService;
import person.clp.stationeryback.service.UserService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
public class StationeryController {

    @Autowired
    StationeryService stationeryService;

    @Autowired
    RecordService recordService;

    @Autowired
    UserService userService;

    @RequestMapping("/getStationeryNumber")
    public List<Stationery> stationery(){
        return stationeryService.getAllStationery();
    }

    //借用文具
    @PostMapping("/borrow/{type}/{count}/{username}")
    public String borrow(@PathVariable("type") String type,@PathVariable Integer count,@PathVariable String username){
        System.out.println("前端输入过来的类型"+type + ",数量："+count);
        stationeryService.reduceStationery(type,count);
        User user = userService.selectUserByUsername(username);
        System.out.println(user.toString());
        Record record = new Record();
        record.setType(type);
        record.setUserId(user.getId());
        record.setCount(count);
        record.setWay(1);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        record.setTime(df.format(new Date()));
        System.out.println(df.format(new Date()));
        recordService.addRecord(record);
        return "借用成功，记得归还哦";
    }

    //归还文具
    @PostMapping("/giveback/{type}/{count}/{username}")
    public String giveback(@PathVariable("type") String type,@PathVariable Integer count,@PathVariable String username){
        System.out.println("前端输入过来的类型"+type + ",数量："+count);
        stationeryService.addStationery(type,count);
        User user = userService.selectUserByUsername(username);
        System.out.println(user.toString());
        Record record = new Record();
        record.setType(type);
        record.setUserId(user.getId());
        record.setCount(count);
        record.setWay(0);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        System.out.println(df.format(new Date()));
        record.setTime(df.format(new Date()));
        recordService.addRecord(record);
        return "归还成功，感谢您的诚信使用";
    }


    @GetMapping("/getBorrowRecord/{username}")
    public List<Record> getAllRecord(@PathVariable("username") String username){
        List<Record> allBorrowRecordByUsername = recordService.getAllBorrowRecordByUsername(username, 1);
        System.out.println("借的记录："+allBorrowRecordByUsername);
        return allBorrowRecordByUsername;
    }

    @GetMapping("/getBackRecord/{username}")
    public List<Record> getBackRecord(@PathVariable("username") String username){
        System.out.println("获取到的用户名："+username);
        List<Record> allBackRecordByUsername = recordService.getAllBackRecordByUsername(username, 0);
        System.out.println("还的记录："+allBackRecordByUsername);
        return allBackRecordByUsername;
    }
}
