package person.clp.stationeryback.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import person.clp.stationeryback.entity.Stationery;
import person.clp.stationeryback.mapper.StationeryMapper;
import person.clp.stationeryback.service.StationeryService;

import java.util.List;

@Service
public class StationeryServiceImpl implements StationeryService {
    @Autowired
    StationeryMapper stationeryMapper;

    @Override
    public List<Stationery> getAllStationery() {
        return stationeryMapper.getStationery();
    }

    @Override
    public void reduceStationery(String type, Integer count) {
        System.out.println("前端输入过来的类型"+type + ",数量："+count);
        if (type.equals("cartridge")){
            System.out.println("借用cartridge");
            List<Stationery> stationery = stationeryMapper.getStationery();
            Stationery stationery1 = stationery.get(0);
            stationeryMapper.updateStationery("cartridge", stationery1.getCartridge()-count);
        }
        if (type.equals("eraser")){
            System.out.println("借用eraser");
            List<Stationery> stationery = stationeryMapper.getStationery();
            Stationery stationery1 = stationery.get(0);
            stationeryMapper.updateStationery("eraser", stationery1.getEraser()-count);
        }
        if (type.equals("paper")){
            System.out.println("借用paper");
            List<Stationery> stationery = stationeryMapper.getStationery();
            Stationery stationery1 = stationery.get(0);
            stationeryMapper.updateStationery("paper", stationery1.getPaper()-count);
        }
        if (type.equals("umbrella")){
            System.out.println("借用umbrella");
            List<Stationery> stationery = stationeryMapper.getStationery();
            Stationery stationery1 = stationery.get(0);
            stationeryMapper.updateStationery("umbrella", stationery1.getUmbrella()-count);
        }
    }

    @Override
    public void addStationery(String type, Integer count) {
        System.out.println("前端输入过来的类型"+type + ",数量："+count);
        if (type.equals("cartridge")){
            System.out.println("归还cartridge");
            List<Stationery> stationery = stationeryMapper.getStationery();
            Stationery stationery1 = stationery.get(0);
            stationeryMapper.updateStationery("cartridge", stationery1.getCartridge()+count);
        }
        if (type.equals("eraser")){
            System.out.println("归还eraser");
            List<Stationery> stationery = stationeryMapper.getStationery();
            Stationery stationery1 = stationery.get(0);
            stationeryMapper.updateStationery("eraser", stationery1.getEraser()+count);
        }
        if (type.equals("paper")){
            System.out.println("归还paper");
            List<Stationery> stationery = stationeryMapper.getStationery();
            Stationery stationery1 = stationery.get(0);
            stationeryMapper.updateStationery("paper", stationery1.getPaper()+count);
        }
        if (type.equals("umbrella")){
            System.out.println("归还umbrella");
            List<Stationery> stationery = stationeryMapper.getStationery();
            Stationery stationery1 = stationery.get(0);
            stationeryMapper.updateStationery("umbrella", stationery1.getUmbrella()+count);
        }
    }
}
