package com.huluxia.mojang.tileentity;

import com.huluxia.mojang.converter.InventorySlot;
import java.util.List;

public class ContainerTileEntity extends TileEntity {
    private List<InventorySlot> items;

    public int getContainerSize() {
        return 27;
    }

    public List<InventorySlot> getItems() {
        return this.items;
    }

    public void setItems(List<InventorySlot> paramList) {
        this.items = paramList;
    }
}
