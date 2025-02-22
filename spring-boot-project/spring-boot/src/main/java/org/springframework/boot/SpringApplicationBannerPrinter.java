/*
 * Copyright 2012-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;

import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;

/**
 * Class used by {@link SpringApplication} to print the application banner.
 *
 * @author Phillip Webb
 */
class SpringApplicationBannerPrinter {

	static final String BANNER_LOCATION_PROPERTY = "spring.banner.location";

	static final String BANNER_IMAGE_LOCATION_PROPERTY = "spring.banner.image.location";

	static final String DEFAULT_BANNER_LOCATION = "banner.txt";

	static final String[] IMAGE_EXTENSION = { "gif", "jpg", "png" };

	private static final Banner DEFAULT_BANNER = new SpringBootBanner();

	private final ResourceLoader resourceLoader;

	private final Banner fallbackBanner;

	SpringApplicationBannerPrinter(ResourceLoader resourceLoader, Banner fallbackBanner) {
		this.resourceLoader = resourceLoader;
		this.fallbackBanner = fallbackBanner;
	}

	Banner print(Environment environment, Class<?> sourceClass, Log logger) {
		// 获取 Banner 打印对象
		Banner banner = getBanner(environment);
		try {
			// 通过 createStringFromBanner 方法将 Banner 转换为字符串，并通过 logger 输出到日志中
			logger.info(createStringFromBanner(banner, environment, sourceClass));
		}
		catch (UnsupportedEncodingException ex) {
			logger.warn("Failed to create String for banner", ex);
		}
		return new PrintedBanner(banner, sourceClass);
	}

	/**
	 * 打印横幅（banner）到指定的输出流。
	 *
	 * @param environment 该应用程序的环境
	 * @param sourceClass 应用程序的主类
	 * @param out         用于打印横幅的输出流
	 * @return 一个包含横幅和主类信息的 PrintedBanner 对象
	 */
	Banner print(Environment environment, Class<?> sourceClass, PrintStream out) {
		// 从环境中获取横幅对象
		Banner banner = getBanner(environment);

		// 打印横幅到指定的输出流
		banner.printBanner(environment, sourceClass, out);

		// 返回一个包含横幅和主类信息的 PrintedBanner 对象
		return new PrintedBanner(banner, sourceClass);
	}

	private Banner getBanner(Environment environment) {
		// 创建一个 Banners 对象，用于存储多个 Banner
		Banners banners = new Banners();

		// 获取图片 Banner 并添加到 Banners 对象中（如果不为空）
		banners.addIfNotNull(getImageBanner(environment));
		// 获取文本 Banner 并添加到 Banners 对象中（如果不为空）
		banners.addIfNotNull(getTextBanner(environment));

		// 如果至少有一个 banner，返回 Banners 对象
		if (banners.hasAtLeastOneBanner()) {
			return banners;
		}

		// 如果没有找到任何 banner，且存在备用 banner，返回备用 banner
		if (this.fallbackBanner != null) {
			return this.fallbackBanner;
		}

		// 没有找到任何 Banner，则直接返回默认 Banner
		return DEFAULT_BANNER;
	}

	private Banner getTextBanner(Environment environment) {
		// 获取文本 Banner 的资源位置，如果未指定则使用默认位置
		String location = environment.getProperty(BANNER_LOCATION_PROPERTY, DEFAULT_BANNER_LOCATION);
		// 从资源加载器中获取指定位置的资源
		Resource resource = this.resourceLoader.getResource(location);
		try {
			// 检查资源是否存在并且资源的URL不包含"liquibase-core"
			if (resource.exists() && !resource.getURL().toExternalForm().contains("liquibase-core")) {
				return new ResourceBanner(resource);
			}
		}
		catch (IOException ex) {
			// Ignore
		}
		return null;
	}

	private Banner getImageBanner(Environment environment) {
		String location = environment.getProperty(BANNER_IMAGE_LOCATION_PROPERTY);
		if (StringUtils.hasLength(location)) {
			Resource resource = this.resourceLoader.getResource(location);
			return resource.exists() ? new ImageBanner(resource) : null;
		}
		for (String ext : IMAGE_EXTENSION) {
			Resource resource = this.resourceLoader.getResource("banner." + ext);
			if (resource.exists()) {
				return new ImageBanner(resource);
			}
		}
		return null;
	}

	private String createStringFromBanner(Banner banner, Environment environment, Class<?> mainApplicationClass)
			throws UnsupportedEncodingException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		banner.printBanner(environment, mainApplicationClass, new PrintStream(baos));
		String charset = environment.getProperty("spring.banner.charset", "UTF-8");
		return baos.toString(charset);
	}

	/**
	 * {@link Banner} comprised of other {@link Banner Banners}.
	 */
	private static class Banners implements Banner {

		private final List<Banner> banners = new ArrayList<>();

		void addIfNotNull(Banner banner) {
			if (banner != null) {
				this.banners.add(banner);
			}
		}

		boolean hasAtLeastOneBanner() {
			return !this.banners.isEmpty();
		}

		@Override
		public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
			for (Banner banner : this.banners) {
				banner.printBanner(environment, sourceClass, out);
			}
		}

	}

	/**
	 * Decorator that allows a {@link Banner} to be printed again without needing to
	 * specify the source class.
	 */
	private static class PrintedBanner implements Banner {

		private final Banner banner;

		private final Class<?> sourceClass;

		PrintedBanner(Banner banner, Class<?> sourceClass) {
			this.banner = banner;
			this.sourceClass = sourceClass;
		}

		@Override
		public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
			sourceClass = (sourceClass != null) ? sourceClass : this.sourceClass;
			this.banner.printBanner(environment, sourceClass, out);
		}

	}

}
