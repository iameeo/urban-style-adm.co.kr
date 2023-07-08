package com.danalent.dashboard.dao;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository("dashBoardMapper")
public abstract interface DashBoardMapper {
	
	// selectCombineShopList
	public List<HashMap> test(HashMap<String, Object> paramMap);
	
	// updateCombineNaverImg
	public Integer test2(HashMap<String, Object> paramMap);
	
	// selectCombineShopList
	public List<HashMap> selectCombineShopList(HashMap<String, Object> paramMap);

	// selectCombineProductList
	public List<HashMap> selectCombineProductList(HashMap<String, Object> paramMap);

	// selectCombineProductExcelList
	public List<HashMap> selectCombineProductExcelList(HashMap<String, Object> paramMap);

	// updateCombineNaverImg
	public Integer updateCombineNaverImg(HashMap<String, Object> paramMap);

	// selectCombineItemCheck
	public Integer selectCombineItemCheck(HashMap<String, Object> paramMap);

	// insertCombineProduct
	public Integer insertCombineProduct(HashMap<String, Object> paramMap);

	// insertCombineDetailImg
	public Integer insertCombineDetailImg(HashMap<String, Object> paramMap);

	public List<HashMap> noneNaverApi(HashMap<String, Object> paramMap);

	public List<HashMap> titleImgList(HashMap<String, Object> paramMap);
	
	// selectCombineProductExcelList
	public List<HashMap> selectGodoAdminList(HashMap<String, Object> paramMap);
	
	// updateGodoAdminChange
	public Integer updateGodoAdminChange(HashMap<String, Object> paramMap);

	public List<HashMap> selectReTitleList(HashMap<String, Object> paramMap);

	public Integer updateReTitleUpdate(HashMap<String, Object> paramMap);

	public Integer deleteImgCombine(HashMap<String, Object> paramMap);
}