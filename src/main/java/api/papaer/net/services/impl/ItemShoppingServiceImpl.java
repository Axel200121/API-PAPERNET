package api.papaer.net.services.impl;

import api.papaer.net.entities.ItemShoppingEntity;
import api.papaer.net.mappers.ItemShoppingMapper;
import api.papaer.net.repositories.ItemShoppingRepository;
import api.papaer.net.services.ItemShoppingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemShoppingServiceImpl implements ItemShoppingService {

    @Autowired
    private ItemShoppingRepository itemShoppingRepository;

    @Autowired
    private ItemShoppingMapper itemShoppingMapper;

    @Override
    public ItemShoppingEntity executeSaveItemShopping(List<ItemShoppingEntity> itemShoppingEntityList) {
        return null;
    }
}
