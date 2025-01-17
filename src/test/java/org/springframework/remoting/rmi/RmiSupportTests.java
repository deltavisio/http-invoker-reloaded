/*
 * Copyright 2002-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.springframework.remoting.rmi;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;
import org.springframework.remoting.support.RemoteInvocation;

/**
 * @author Juergen Hoeller
 * @since 16.05.2003
 */
class RmiSupportTests {
  @Test
  void remoteInvocation() throws NoSuchMethodException {
    // let's see if the remote invocation object works

    final RemoteBean rb = new RemoteBean();
    final Method setNameMethod = rb.getClass().getDeclaredMethod("setName", String.class);

    MethodInvocation mi = new MethodInvocation() {
      @Override
      public Object[] getArguments() {
        return new Object[] {"bla"};
      }

      @Override
      public Method getMethod() {
        return setNameMethod;
      }

      @Override
      public AccessibleObject getStaticPart() {
        return setNameMethod;
      }

      @Override
      public Object getThis() {
        return rb;
      }

      @Override
      public Object proceed() throws Throwable {
        throw new UnsupportedOperationException();
      }
    };

    RemoteInvocation inv = new RemoteInvocation(mi);

    assertThat(inv.getMethodName()).isEqualTo("setName");
    assertThat(inv.getArguments()[0]).isEqualTo("bla");
    assertThat(inv.getParameterTypes()[0]).isEqualTo(String.class);

    // this is a bit BS, but we need to test it
    inv = new RemoteInvocation();
    inv.setArguments(new Object[] {"bla"});
    assertThat(inv.getArguments()[0]).isEqualTo("bla");
    inv.setMethodName("setName");
    assertThat(inv.getMethodName()).isEqualTo("setName");
    inv.setParameterTypes(new Class<?>[] {String.class});
    assertThat(inv.getParameterTypes()[0]).isEqualTo(String.class);

    inv = new RemoteInvocation("setName", new Class<?>[] {String.class}, new Object[] {"bla"});
    assertThat(inv.getArguments()[0]).isEqualTo("bla");
    assertThat(inv.getMethodName()).isEqualTo("setName");
    assertThat(inv.getParameterTypes()[0]).isEqualTo(String.class);
  }

  public interface IBusinessBean {

    void setName(String name);
  }

  public interface IRemoteBean extends Remote {

    void setName(String name) throws RemoteException;
  }

  public interface IWrongBusinessBean {

    void setOtherName(String name);
  }

  public static class RemoteBean implements IRemoteBean {

    @SuppressWarnings("unused")
    private static String name;

    @Override
    public void setName(String nam) throws RemoteException {
      if (nam != null && nam.endsWith("Exception")) {
        RemoteException rex;
        try {
          Class<?> exClass = Class.forName(nam);
          Constructor<?> ctor = exClass.getConstructor(String.class);
          rex = (RemoteException) ctor.newInstance("myMessage");
        } catch (Exception ex) {
          throw new RemoteException("Illegal exception class name: " + nam, ex);
        }
        throw rex;
      }
      name = nam;
    }
  }

}
