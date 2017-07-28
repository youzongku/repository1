package entity.purchase.enums;

/**
 * Created by luwj on 2015/12/30.
 */
public enum  Warehouse {

    广州仓(1),

    深圳仓(2),

    郑州仓(3);

    private int type;

    private Warehouse(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
