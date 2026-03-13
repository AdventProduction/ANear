package kz.ifihtich.anear;

public class ConfigManager {

    public String getString(String path){
        return Utils.color(ANear.getInstance().getConfig().getString(path));
    }
}
