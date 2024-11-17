package com.smartcontact.helper;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;

@Component
public class SessionHelper {

		public void removeSessionMessage() {
			
			try {
				
				HttpSession httpSession = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
				
				httpSession.removeAttribute("message");
				
			} catch (Exception e) {
				
				e.printStackTrace();
			}
			
		}
}
