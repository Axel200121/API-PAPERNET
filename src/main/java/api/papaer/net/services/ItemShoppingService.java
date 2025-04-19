package api.papaer.net.services;

import api.papaer.net.entities.ItemShoppingEntity;

import java.util.List;

public interface ItemShoppingService {

    ItemShoppingEntity executeSaveItemShopping(List<ItemShoppingEntity> itemShoppingEntityList);
}
