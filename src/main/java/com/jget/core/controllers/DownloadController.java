package com.jget.core.controllers;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.jget.core.ErrorMessages;
import com.jget.core.configuration.ConfigurationConstant;
import com.jget.core.configuration.ConfigurationManager;
import com.jget.core.download.DownloadManager;
import com.jget.core.download.DownloadManagerStatus;
import com.jget.core.manifest.Manifest;
import com.jget.core.manifest.ManifestProvider;
import com.jget.core.utils.manifest.ManifestUtils;

@RestController
@RequestMapping(value = "/api/project")
public class DownloadController {

	@Autowired
	DownloadManager downloadManager;

	@Autowired
	ConfigurationManager configurationManager;

	@RequestMapping(value = "/downloadManagerStatus", method = RequestMethod.GET)
	public ResponseEntity<DownloadManagerStatus> getDownloadManagerStatus (HttpServletRequest request) {
		logger.info ("Service called: getDownloadStatus()");
		return ResponseEntity.ok (downloadManager.getDownloadStatus ());
	}

	@RequestMapping(value = "/isActive", method = RequestMethod.GET)
	public ResponseEntity<Boolean> isActive (HttpServletRequest request) {
		logger.info ("Service called: isActive()");
		return ResponseEntity.ok (downloadManager.isCurrentlyRunning ());
	}

	@RequestMapping(value = "/commenceDownload")
	public ResponseEntity<String> startDownload (HttpServletRequest request) {
		logger.info ("Service called: startDownload()");
		if (ManifestProvider.getCurrentManifest () == null)
			return ResponseEntity.status (HttpStatus.NOT_FOUND).body (null);

		downloadManager.commenceDownload ();
		return ResponseEntity.ok ("Download of manifest commenced, id: " + ManifestProvider.getCurrentManifest ().getId ());
	}

	@RequestMapping(value = "/cancelDownload", method = RequestMethod.GET)
	public ResponseEntity<String> cancelDownload (HttpServletRequest request) {
		logger.info ("Service called: cancelDownload()");
		if (ManifestProvider.getCurrentManifest () == null || !downloadManager.isCurrentlyRunning ())
			return ResponseEntity.ok ("Unable to cancel project as none are currently running");

		downloadManager.cancelDownload ();
		return ResponseEntity.ok ("Canceling download of project: " + ManifestProvider.getCurrentManifest ().getId ());

	}

	@RequestMapping(value = "/setCurrentManifest/{id}", method = RequestMethod.GET)
	public ResponseEntity<String> setCurrentManifest (@PathVariable String id) {
		logger.info ("Service called: setCurrentManifest({})", id);

		if (downloadManager.isCurrentlyRunning ()) {
			return ResponseEntity.status (HttpStatus.CONFLICT).body (ErrorMessages.METHOD_UNAVAILABLE_DOWNLOAD_MANAGER_RUNNING);
		}

		for (Manifest manifest : ManifestProvider.getManifests ()) {
			if (manifest.getId ().toString ().equals (id)) {
				ManifestProvider.setCurrentManifest (manifest);
				return ResponseEntity.status (HttpStatus.ACCEPTED).body (null);
			}
		}

		return ResponseEntity.status (HttpStatus.NOT_FOUND).body (ErrorMessages.MANIFEST_NOT_FOUND_WP + id);
	}

	@RequestMapping(value = "/getCurrentManifest", method = RequestMethod.GET)
	public ResponseEntity<Manifest> getCurrentManifest () {
		logger.info ("Service called: getCurrentManifest()");
		return ResponseEntity.ok (ManifestProvider.getCurrentManifest ());
	}

	@RequestMapping(value = "/get")
	@ResponseBody
	public ResponseEntity<Set<Manifest>> getProjects (HttpServletRequest request) {
		logger.info ("Service called: getProjects(), from IP: {}", request.getRemoteAddr ());
		return ResponseEntity.ok (ManifestProvider.getManifests ());
	}

	@RequestMapping(value = "/get/{id}")
	@ResponseBody
	public ResponseEntity<Manifest> getProject (@PathVariable String id, HttpServletRequest request) {
		logger.info ("Service called: getProject({}), from IP: {}", id, request.getRemoteAddr ());

		for (Manifest manifest : ManifestProvider.getManifests ()) {
			if (manifest.getId ().toString ().equals (id))
				return ResponseEntity.ok (manifest);
		}

		return ResponseEntity.status (HttpStatus.NOT_FOUND).body (null);
	}

	@RequestMapping(value = "/delete/{id}")
	@ResponseBody
	public ResponseEntity<Manifest> deleteProject (@PathVariable String id, HttpServletRequest request) {
		logger.info ("Service called: deleteProject({}), from IP: {}", id, request.getRemoteAddr ());

		Set<Manifest> manifests = ManifestProvider.getManifests ();

		for (Manifest manifest : ManifestProvider.getManifests ()) {
			if (manifest.getId ().toString ().equals (id)) {
				manifests.remove (manifest);
				ManifestProvider.setManifests (manifests);
				return ResponseEntity.status (HttpStatus.OK).body (null);
			}
		}
		return ResponseEntity.status (HttpStatus.NOT_FOUND).body (null);
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Manifest> create (@RequestBody Manifest inputManifest, HttpServletRequest request) {
		logger.info ("Service called: create(), from IP: {}", request.getRemoteAddr ());

		if (ManifestUtils.manifestNameExists (inputManifest.getName ())) {
			return ResponseEntity.status (HttpStatus.CONFLICT).body (null);
		}

		if (inputManifest.getRootDir () == null || StringUtils.isEmpty (inputManifest.getRootDir ().toString ())) {
			Path rootPath = Paths.get (configurationManager.getValue (ConfigurationConstant.FILESTORE).get ().toString ());
			inputManifest.setRootDir (rootPath);
		}

		if (!inputManifest.validate ()) {
			logger.info ("Invalid manifest provided");
			return ResponseEntity.status (HttpStatus.BAD_REQUEST).body (null);
		}

		ManifestProvider.getManifests ().add (inputManifest);
		return ResponseEntity.ok (inputManifest);

	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Manifest> edit (@RequestBody Manifest inputManifest, HttpServletRequest request) {
		logger.info ("Service called: edit(), from IP: {}", request.getRemoteAddr ());

		if (!ManifestUtils.manifestNameExists (inputManifest.getName ())) {
			logger.info ("Unable to find manifest name");
			return ResponseEntity.status (HttpStatus.NOT_FOUND).body (null);
		}
		if (ManifestProvider.getCurrentManifest () != null && ManifestProvider.getCurrentManifest ().getId ().equals (inputManifest.getId ())) {
			return ResponseEntity.status (HttpStatus.BAD_REQUEST).body (null);
		}

		Set<Manifest> newManifests = new HashSet<Manifest>();

		for (Manifest manifest : ManifestProvider.getManifests ()) {

			if (!manifest.getId ().equals (inputManifest.getId ())) {
				newManifests.add (manifest);
			}
		}

		if (newManifests.size () == ManifestProvider.getManifests ().size ()) {
			logger.info ("Unable to find manifest id");
			return ResponseEntity.status (HttpStatus.NOT_FOUND).body (null);
		}

		newManifests.add (inputManifest);
		ManifestProvider.setManifests (newManifests);

		return ResponseEntity.ok (inputManifest);

	}

	private static final Logger logger = LoggerFactory.getLogger (DownloadController.class);

}
