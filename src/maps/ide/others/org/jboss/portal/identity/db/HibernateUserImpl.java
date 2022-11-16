/*
* JBoss, a division of Red Hat
* Copyright 2006, Red Hat Middleware, LLC, and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.jboss.portal.identity.db;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jboss.portal.common.p3p.P3PConstants;
import org.jboss.portal.common.util.Tools;
import org.jboss.portal.identity.ProfileMap;
import org.jboss.portal.identity.User;

import com.genband.m5.maps.common.entity.Organization;

/**
 * User interface implementation.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet </a>
 * @author <a href="mailto:theute@jboss.org">Thomas Heute </a>
 * @author <a href="mailto:mageshbk@jboss.com">Magesh Kumar Bojan </a>
 * @author <a href="mailto:boleslaw dot dawidowicz at jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 5448 $
 */
public class HibernateUserImpl
   implements User
{

   private static final org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(HibernateUserImpl.class);

   static final Map ACCESSORS = HibernateUserImpl.buildAccessors();

   private static Map buildAccessors()
   {
      Map map = new HashMap();

      // Map attributes defined by the JSR 168 spec P3P.
      map.put(P3PConstants.INFO_USER_NAME_NICKNAME, new StringPropertyAccessor(P3PConstants.INFO_USER_NAME_NICKNAME, "userName", false, false));
      map.put(P3PConstants.INFO_USER_BUSINESS_INFO_ONLINE_EMAIL, new StringPropertyAccessor(P3PConstants.INFO_USER_BUSINESS_INFO_ONLINE_EMAIL, "realEmail", true, true));
      map.put(P3PConstants.INFO_USER_NAME_GIVEN, new StringPropertyAccessor(P3PConstants.INFO_USER_NAME_GIVEN, "givenName", true, true));
      map.put(P3PConstants.INFO_USER_NAME_FAMILY, new StringPropertyAccessor(P3PConstants.INFO_USER_NAME_FAMILY, "familyName", true, true));

      // Map attributes specific to JBoss Portal
      map.put(org.jboss.portal.identity.User.INFO_USER_EMAIL_FAKE, new StringPropertyAccessor(org.jboss.portal.identity.User.INFO_USER_EMAIL_FAKE, "fakeEmail", true, true));
      map.put(org.jboss.portal.identity.User.INFO_USER_REGISTRATION_DATE, new DatePropertyAccessor(org.jboss.portal.identity.User.INFO_USER_REGISTRATION_DATE, "registrationDate", true, false));
      map.put(org.jboss.portal.identity.User.INFO_USER_VIEW_EMAIL_VIEW_REAL, new BooleanPropertyAccessor(org.jboss.portal.identity.User.INFO_USER_VIEW_EMAIL_VIEW_REAL, "viewRealEmail", true, false));
      map.put(org.jboss.portal.identity.User.INFO_USER_ENABLED, new BooleanPropertyAccessor(org.jboss.portal.identity.User.INFO_USER_ENABLED, "enabled", true, false));

      //
      return Collections.unmodifiableMap(map);
   }

   protected ProfileMap profileMap;

   /*
    * P3P mapped persistent fields.
    */

   protected String userName;
   protected String givenName;
   protected String familyName;
   protected String realEmail;

   /*
    * Non mapped persistent fields.
    */

   protected Long key;
   protected boolean enabled;
   protected String password;

   /*
    * Extension mapped persistent fields.
    */

   protected String fakeEmail;
   protected boolean viewRealEmail;
   protected Date registrationDate;

   /*
    * Persistent associations
    */

   protected Map dynamic;
   protected Set roles;
   
   //GB organization
   protected Organization merchantAccount;

   /**
    *
    */
   public HibernateUserImpl()
   {
      this.key = null;
      this.userName = null;
      this.dynamic = null;
      this.roles = null;
      this.registrationDate = null;
      this.enabled = false;
      this.profileMap = new org.jboss.portal.identity.db.ProfileMapImpl(this);
   }

   /**
    *
    */
   public HibernateUserImpl(String userName)
   {
      this.key = null;
      this.userName = userName;
      this.dynamic = new HashMap();
      this.roles = new HashSet();
      this.registrationDate = new Date();
      this.enabled = false;
      this.profileMap = new ProfileMapImpl(this);
      //PR 51046
      this.merchantAccount = new Organization();
   }

   /** Called by hibernate. */
   public Long getKey()
   {
      return key;
   }

   /** Called by hibernate. */
   protected void setKey(Long key)
   {
      this.key = key;
   }

   /** Called by hibernate. */
   protected void setUserName(String userName)
   {
      this.userName = userName;
   }

   /** Called by Hibernate. */
   protected Map getDynamic()
   {
      return dynamic;
   }

   /** Called by Hibernate. */
   protected void setDynamic(Map dynamic)
   {
      this.dynamic = dynamic;
   }

   public ProfileMap getProfileMap()
   {
      return profileMap;
   }

   // User implementation **********************************************************************************************

   /**
    *
    */
   public Object getId()
   {
      return key;
   }

   /**
    *
    */
   public String getUserName()
   {
      return userName;
   }

   /**
    *
    */
   public String getGivenName()
   {
      return givenName;
   }

   public void setGivenName(String givenName)
   {
      this.givenName = givenName;
   }

   /**
    *
    */
   public String getFamilyName()
   {
      return familyName;
   }

   public void setFamilyName(String familyName)
   {
      this.familyName = familyName;
   }

   public void updatePassword(String password)
   {
      this.password = Tools.md5AsHexString(password);
   }

   /**
    *
    */
   public String getRealEmail()
   {
      return realEmail;
   }

   /**
    *
    */
   public void setRealEmail(String realEmail)
   {
      this.realEmail = realEmail;
   }

   /**
    *
    */
   public String getFakeEmail()
   {
      return fakeEmail;
   }

   /**
    *
    */
   public void setFakeEmail(String fakeEmail)
   {
      this.fakeEmail = fakeEmail;
   }

   /**
    *
    */
   public Date getRegistrationDate()
   {
      return registrationDate;
   }

   /**
    *
    */
   public void setRegistrationDate(Date registrationDate)
   {
      this.registrationDate = registrationDate;
   }

   /**
    *
    */
   public boolean getViewRealEmail()
   {
      return viewRealEmail;
   }

   /**
    *
    */
   public void setViewRealEmail(boolean viewRealEmail)
   {
      this.viewRealEmail = viewRealEmail;
   }

   /**
    *
    */
   public boolean getEnabled()
   {
      return enabled;
   }

   /**
    *
    */
   public void setEnabled(boolean enable)
   {
      this.enabled = enable;
   }

   public String getPassword()
   {
      return password;
   }

   public void setPassword(String password)
   {
      this.password = password;
   }

   /** Returns the roles related to this user. */
   public Set getRoles()
   {
      return roles;
   }

   /** Update the roles. */
   public void setRoles(Set roles)
   {
      this.roles = roles;
   }

    public Organization getMerchantAccount() {
		return merchantAccount;
	}

	public void setMerchantAccount(Organization merchantAccount) {
		this.merchantAccount = merchantAccount;
	}

public boolean validatePassword(String password)
   {
      if (password != null)
      {
         String hashedPassword = Tools.md5AsHexString(password);
         return hashedPassword.equals(this.password);
      }
      return false;
   }

   /**
    *
    */
   public String toString()
   {
      return "User[" + key + "," + userName + "]";
   }


   /** An accessor that maps a user field to a property name. */
   static abstract class PropertyAccessor
   {

      protected final String propertyName;
      protected final Field field;
      protected final boolean writable;
      protected final boolean nullable;

      public PropertyAccessor(String propertyName, String fieldName, boolean writable, boolean nullable)
      {
         try
         {
            this.propertyName = propertyName;
            this.writable = writable;
            this.field = HibernateUserImpl.class.getDeclaredField(fieldName);
            this.nullable = nullable;
         }
         catch (NoSuchFieldException e)
         {
            throw new Error(e);
         }
      }

      public String getPropertyName()
      {
         return propertyName;
      }

      public boolean isNullable()
      {
         return nullable;
      }

      public boolean isWritable()
      {
         return writable;
      }

      /**
       * @param instance the user instance
       * @param value   the value
       * @throws IllegalArgumentException if the string cannot be converted to an object
       */
      public void set(Object instance, Object value) throws IllegalArgumentException
      {
         try
         {
            if (value == null)
            {
               field.set(instance, null);
            }

            if (value instanceof String)
            {

               Object object = toObject((String)value);
               field.set(instance, object);
               
            }
            else
            {
               field.set(instance,value);
            }
         }
         catch (IllegalAccessException e)
         {
            throw new Error(e);
         }
      }

      /**
       * @param instance the user instance
       * @return the converted value
       * @throws IllegalArgumentException if the object cannot be converted to a string
       */
      public Object get(Object instance) throws IllegalArgumentException
      {
         try
         {
            Object object = field.get(instance);
            if (object == null)
            {
               return null;
            }
            else
            {
               return toString(object);
            }
         }
         catch (IllegalAccessException e)
         {
            throw new Error(e);
         }
      }

      /**
       * Perform the to object conversion.
       *
       * @param value the value to convert
       * @return the converted value
       * @throws IllegalArgumentException if the string cannot be converted to an object
       */
      protected abstract Object toObject(String value) throws IllegalArgumentException;

      /**
       * Perform the to strong conversion.
       *
       * @param value the value to convert
       * @return the converted value
       * @throws IllegalArgumentException if the object cannot be converted to a string
       */
      protected abstract String toString(Object value);

      public String toString()
      {
         return "PropertyAccessor[" + propertyName + "," + field + "]";
      }
   }

   static class StringPropertyAccessor extends PropertyAccessor
   {
      public StringPropertyAccessor(String propertyName, String fieldName, boolean writable, boolean nullable)
      {
         super(propertyName, fieldName, writable, nullable);
      }

      protected Object toObject(String value)
      {
         return value;
      }

      protected String toString(Object value)
      {
         return (String)value;
      }
   }

   static class BooleanPropertyAccessor extends PropertyAccessor
   {
      public BooleanPropertyAccessor(String propertyName, String fieldName, boolean writable, boolean nullable)
      {
         super(propertyName, fieldName, writable, nullable);
      }

      protected Object toObject(String value) throws IllegalArgumentException
      {
         if ("true".equalsIgnoreCase(value))
         {
            return Boolean.TRUE;
         }
         else if ("false".equalsIgnoreCase(value))
         {
            return Boolean.FALSE;
         }
         else
         {
            throw new IllegalArgumentException("The value " + value + " cannot be converted to boolean for accessor " + toString());
         }
      }

      protected String toString(Object value)
      {
         return value.toString();
      }

      //workaround to return Date object
      public Object get(Object instance) throws IllegalArgumentException
      {
         try
         {
            return field.get(instance);
         }
         catch (IllegalAccessException e)
         {
            throw new Error(e);
         }
      }


   }

   static class DatePropertyAccessor extends PropertyAccessor
   {
      private static final ThreadLocal formatLocal = new ThreadLocal()
      {
         protected Object initialValue()
         {
            return new SimpleDateFormat();
         }
      };

      public DatePropertyAccessor(String propertyName, String fieldName, boolean writable, boolean nullable)
      {
         super(propertyName, fieldName, writable, nullable);
      }

      protected Object toObject(String value) throws IllegalArgumentException
      {
         try
         {
            DateFormat format = (DateFormat)HibernateUserImpl.DatePropertyAccessor.formatLocal.get();
            Date date = format.parse(value);
            return date;
         }
         catch (ParseException e)
         {
            throw new IllegalArgumentException();
         }
      }

      protected String toString(Object value)
      {
         Date date = (Date)value;
         DateFormat format = (DateFormat)HibernateUserImpl.DatePropertyAccessor.formatLocal.get();
         return format.format(date);
      }

      //workaround to return Date object
      public Object get(Object instance) throws IllegalArgumentException
      {
         try
         {
            return field.get(instance);

         }
         catch (IllegalAccessException e)
         {
            throw new Error(e);
         }
      }
   }
}
