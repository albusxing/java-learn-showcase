package com.albusxing.showcase.web;

import com.albusxing.showcase.domain.City;
import com.albusxing.showcase.mapper.CityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * @author Albusxing
 */

@RestController
@RequiredArgsConstructor
public class CityController {

	private final CityMapper cityMapper;

	@GetMapping("/cities/{id}")
	public City byId(@PathVariable("id") Long id) {
		return cityMapper.selectById(id);
	}

	@GetMapping("/cities")
	public List<City> cities() {
		return cityMapper.selectAll();
	}

}
