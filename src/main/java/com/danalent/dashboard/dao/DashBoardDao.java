package com.danalent.dashboard.dao;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

@Service("dashBoardDao")
public class DashBoardDao {
	@Resource(name = "dashBoardMapper")
	private DashBoardMapper dashBoardMapper;
	
	// selectCombineShopList
	public List<HashMap> test(HashMap<String, Object> paramMap) {
		return this.dashBoardMapper.test(paramMap);
	};
	
	// updateCombineNaverImg
	public Integer test2(HashMap<String, Object> paramMap) {
		return this.dashBoardMapper.test2(paramMap);
	};

	// selectCombineShopList
	public List<HashMap> selectCombineShopList(HashMap<String, Object> paramMap) {
		return this.dashBoardMapper.selectCombineShopList(paramMap);
	};

	// selectCombineProductList
	public List<HashMap> selectCombineProductList(HashMap<String, Object> paramMap) {
		return this.dashBoardMapper.selectCombineProductList(paramMap);
	};

	// selectCombineProductExcelList
	public List<HashMap> selectCombineProductExcelList(HashMap<String, Object> paramMap) {
		return this.dashBoardMapper.selectCombineProductExcelList(paramMap);
	};

	// updateCombineNaverImg
	public Integer updateCombineNaverImg(HashMap<String, Object> paramMap) {
		return this.dashBoardMapper.updateCombineNaverImg(paramMap);
	};

	// selectCombineItemCheck
	public Integer selectCombineItemCheck(HashMap<String, Object> paramMap) {
		return this.dashBoardMapper.selectCombineItemCheck(paramMap);
	};

	// insertCombineProduct
	public Integer insertCombineProduct(HashMap<String, Object> paramMap) {
		return this.dashBoardMapper.insertCombineProduct(paramMap);
	};

	// insertCombineDetailImg
	public Integer insertCombineDetailImg(HashMap<String, Object> paramMap) {
		return this.dashBoardMapper.insertCombineDetailImg(paramMap);
	}

	public List<HashMap> noneNaverApi(HashMap<String, Object> paramMap) {
		return this.dashBoardMapper.noneNaverApi(paramMap);
	};
	
	public List<HashMap> titleImgList(HashMap<String, Object> paramMap) {
		return this.dashBoardMapper.titleImgList(paramMap);
	};
	
	// selectGodoAdminList
	public List<HashMap> selectGodoAdminList(HashMap<String, Object> paramMap) {
		return this.dashBoardMapper.selectGodoAdminList(paramMap);
	};
	
	// updateGodoAdminChange
	public Integer updateGodoAdminChange(HashMap<String, Object> paramMap) {
		return this.dashBoardMapper.updateGodoAdminChange(paramMap);
	}

	public List<HashMap> selectReTitleList(HashMap<String, Object> paramMap) {
		return this.dashBoardMapper.selectReTitleList(paramMap);
	}

	public Integer updateReTitleUpdate(HashMap<String, Object> paramMap) {
		return this.dashBoardMapper.updateReTitleUpdate(paramMap);		
	};
	
	public Integer deleteImgCombine(HashMap<String, Object> paramMap) {
		return this.dashBoardMapper.deleteImgCombine(paramMap);		
	};
}