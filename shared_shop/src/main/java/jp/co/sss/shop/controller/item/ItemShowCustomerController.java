package jp.co.sss.shop.controller.item;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jp.co.sss.shop.bean.ItemBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.ReviewsRepository;
import jp.co.sss.shop.util.BeanCopy;
import jp.co.sss.shop.util.Constant;

/**
 * 商品管理 一覧表示機能(一般会員用)のコントローラクラス
 *
 * @author SystemShared
 */
@Controller
public class ItemShowCustomerController {
	/**
	 * 商品情報
	 */
	@Autowired
	ItemRepository itemRepository;

	@Autowired
	ReviewsRepository reviewsRepository;

	/**
	 * トップ画面 表示処理
	 *
	 * @param model    Viewとの値受渡し
	 * @param pageable ページング情報
	 * @return "/" トップ画面へ
	 */
	@RequestMapping(path = "/")
	public String index(Model model, Pageable pageable) {

		return "index";
	}

	@RequestMapping(path = "/item/list/1")
	public String showItem(Model model, Pageable pageable) {
		// 商品情報を全件検索(新着順)
		Page<Item> itemList = itemRepository.findByDeleteFlagOrderByInsertDateDesc(Constant.NOT_DELETED, pageable);

		// エンティティ内の検索結果をJavaBeansにコピー
		List<ItemBean> itemBeanList = BeanCopy.copyEntityToItemBean(itemList.getContent());

		// 商品情報をViewへ渡す
		model.addAttribute("pages", itemList);
		model.addAttribute("items", itemBeanList);
		return "item/list/item_list";
	}

	@RequestMapping(path = "/item/detail/{id}")
	public String showItem(@PathVariable int id, Model model) {

		// 商品IDに該当する商品情報を取得
		Item item = itemRepository.findById(id).orElse(null);

		ItemBean itemBean = new ItemBean();

		// Itemエンティティの各フィールドの値をItemBeanにコピー
		BeanUtils.copyProperties(item, itemBean);

		// 商品情報にカテゴリ名を設定
		itemBean.setCategoryName(item.getCategory().getName());

		Float averageRate = reviewsRepository.findAverageRate(id);
		Float averageRateByPointFive = null;
//		if(averageRate.equals(null)) {
//			averageRate  = (float) ((Math.floor(averageRate * 100)) / 100);
//		}
//		if(averageRate == 5.0) {
//			averageRateByPointFive = (float) 5.0;
//		}else if(4.25 <= averageRate && averageRate < 5.0){
//			averageRateByPointFive = (float) 4.5;
//		}else if(3.75 <= averageRate && averageRate < 4.25){
//			averageRateByPointFive = (float) 4.0;
//		}else if(3.25 <= averageRate && averageRate < 3.75){
//			averageRateByPointFive = (float) 3.5;
//		}else if(2.75 <= averageRate && averageRate < 3.25){
//			averageRateByPointFive = (float) 3.0;
//		}else if(2.25 <= averageRate && averageRate < 2.75){
//			averageRateByPointFive = (float) 2.5;
//		}else if(1.75 <= averageRate && averageRate < 2.25){
//			averageRateByPointFive = (float) 2.0;
//		}else if(1.25 <= averageRate && averageRate < 1.75){
//			averageRateByPointFive = (float) 1.5;
//		}else if(0.75 <= averageRate && averageRate < 1.25){
//			averageRateByPointFive = (float) 1.0;
//		}else if(0.25 <= averageRate && averageRate < 0.75){
//			averageRateByPointFive = (float) 0.5;
//		}else if(0.0 <= averageRate && averageRate < 0.25){
//			averageRateByPointFive = (float) 0.0;
//		}
		itemBean.setAverageRate(averageRate);
		itemBean.setAverageRateByPointFive(averageRateByPointFive);

		// 商品情報をViewへ渡す
		model.addAttribute("item", itemBean);

		model.addAttribute("reviews", reviewsRepository.findByDeleteFlagAndItemId(id));

		return "item/detail/item_detail";
	}

	@RequestMapping(path = "/item/list/{sortType}")
	public String showItemOrderByDate(@PathVariable int sortType, Model model, Pageable pageable) {
		if (sortType == 1) {
			// 商品情報を全件検索(新着順)
			Page<Item> itemList = itemRepository.findByDeleteFlagOrderByInsertDateDesc(Constant.NOT_DELETED, pageable);

			// エンティティ内の検索結果をJavaBeansにコピー
			List<ItemBean> itemBeanList = BeanCopy.copyEntityToItemBean(itemList.getContent());
			// 商品情報をViewへ渡す
			model.addAttribute("pages", itemList);
			model.addAttribute("items", itemBeanList);
		} else if (sortType == 2) {
			Page<Item> itemList = itemRepository.findByDeleteFlagOrderByOrderCount(pageable);

			// エンティティ内の検索結果をJavaBeansにコピー
			List<ItemBean> itemBeanList = BeanCopy.copyEntityToItemBean(itemList.getContent());
			// 商品情報をViewへ渡す
			model.addAttribute("pages", itemList);
			model.addAttribute("items", itemBeanList);

		}
		return "item/list/item_list";
	}

}
