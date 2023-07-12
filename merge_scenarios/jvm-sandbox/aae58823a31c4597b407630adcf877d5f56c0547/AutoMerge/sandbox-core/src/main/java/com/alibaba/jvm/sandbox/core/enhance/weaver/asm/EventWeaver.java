package com.alibaba.jvm.sandbox.core.enhance.weaver.asm;
import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.core.enhance.weaver.CodeLock;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.JSRInlinerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.com.alibaba.jvm.sandbox.spy.Spy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import static com.alibaba.jvm.sandbox.core.util.SandboxStringUtils.toJavaClassName;
import static org.apache.commons.lang3.ArrayUtils.contains;
import static org.apache.commons.lang3.StringUtils.join;

/**
 * 用于Call的代码锁
 */
class CallAsmCodeLock extends AsmCodeLock {
  CallAsmCodeLock(AdviceAdapter aa) {
    super(aa, new int[] { ICONST_2, POP }, new int[] { ICONST_3, POP });
  }
}

/**
 * TryCatch块,用于ExceptionsTable重排序
 */
class AsmTryCatchBlock {
  final Label start;

  final Label end;

  final Label handler;

  final String type;

  AsmTryCatchBlock(Label start, Label end, Label handler, String type) {
    this.start = start;
    this.end = end;
    this.handler = handler;
    this.type = type;
  }
}

/**
 * 方法事件编织者
 * Created by luanjia@taobao.com on 16/7/16.
 */
public class EventWeaver extends ClassVisitor implements Opcodes, AsmTypes, AsmMethods {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final int targetClassLoaderObjectID;

  private final String namespace;

  private final int listenerId;

  private final String targetJavaClassName;

  private final Set<String> signCodes;

  private final Event.Type[] eventTypeArray;

  private final boolean isLineEnable;

  private final boolean hasCallThrows;

  private final boolean hasCallBefore;

  private final boolean hasCallReturn;

  private final boolean isCallEnable;

  public EventWeaver(final int api, final ClassVisitor cv, final String namespace, final int listenerId, final int targetClassLoaderObjectID, final String targetClassInternalName, final Set<String> signCodes, final Event.Type[] eventTypeArray) {
    super(api, cv);
    this.targetClassLoaderObjectID = targetClassLoaderObjectID;
    this.namespace = namespace;
    this.listenerId = listenerId;
    this.targetJavaClassName = toJavaClassName(targetClassInternalName);
    this.signCodes = signCodes;
    this.eventTypeArray = eventTypeArray;
    this.isLineEnable = contains(eventTypeArray, Event.Type.LINE);
    this.hasCallBefore = contains(eventTypeArray, Event.Type.CALL_BEFORE);
    this.hasCallReturn = contains(eventTypeArray, Event.Type.CALL_RETURN);
    this.hasCallThrows = contains(eventTypeArray, Event.Type.CALL_THROWS);
    this.isCallEnable = hasCallBefore || hasCallReturn || hasCallThrows;
  }

  private boolean isMatchedBehavior(final String signCode) {
    return signCodes.contains(signCode);
  }

  private String getBehaviorSignCode(final String name, final String desc) {
    final StringBuilder sb = new StringBuilder(256).append(targetJavaClassName).append("#").append(name).append("(");
    final Type[] methodTypes = Type.getMethodType(desc).getArgumentTypes();

    for (final Type parameterType : methodType.getArgumentTypes()) {
      parameterClassNameArray.add(parameterType.getClassName());
    }
    return 
<<<<<<< /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/left/sandbox-core/src/main/java/com/alibaba/jvm/sandbox/core/enhance/weaver/asm/EventWeaver.java
    targetJavaClassName + "#" + name + "(" + join(parameterClassNameArray, ",") + ")"
=======
    sb.append(")").toString()
>>>>>>> /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/right/sandbox-core/src/main/java/com/alibaba/jvm/sandbox/core/enhance/weaver/asm/EventWeaver.java
    ;
  }

  @Override public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
    final MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
    final String signCode = getBehaviorSignCode(name, desc);
    if (!isMatchedBehavior(signCode)) {
      logger.debug("non-rewrite method {} for listener[id={}];", signCode, listenerId);
      return mv;
    }
    logger.info("rewrite method {} for listener[id={}];event={};", signCode, listenerId, join(eventTypeArray, ","));
    return new ReWriteMethod(api, new JSRInlinerAdapter(mv, access, name, desc, signature, exceptions), access, name, desc) {
      private final Label beginLabel = new Label();

      private final Label endLabel = new Label();

      private boolean isMethodEnter = false;

      private final CodeLock codeLockForTracing = new CallAsmCodeLock(this);

      /**
             * 流程控制
             */
      private void processControl() {
        final Label finishLabel = new Label();
        final Label returnLabel = new Label();
        final Label throwsLabel = new Label();
        dup();
        visitFieldInsn(GETFIELD, ASM_TYPE_SPY_RET, "state", ASM_TYPE_INT);
        dup();
        push(Spy.Ret.RET_STATE_RETURN);
        ifICmp(EQ, returnLabel);
        push(Spy.Ret.RET_STATE_THROWS);
        ifICmp(EQ, throwsLabel);
        goTo(finishLabel);
        mark(returnLabel);
        pop();
        visitFieldInsn(GETFIELD, ASM_TYPE_SPY_RET, "respond", ASM_TYPE_OBJECT);
        checkCastReturn(Type.getReturnType(desc));
        goTo(finishLabel);
        mark(throwsLabel);
        visitFieldInsn(GETFIELD, ASM_TYPE_SPY_RET, "respond", ASM_TYPE_OBJECT);
        checkCast(ASM_TYPE_THROWABLE);
        throwException();
        mark(finishLabel);
        pop();
      }

      private void loadClassLoader() {
        push(targetClassLoaderObjectID);
      }

      @Override protected void onMethodEnter() {
        codeLockForTracing.lock(new CodeLock.Block() {
          @Override public void code() {
            loadArgArray();
            dup();
            push(namespace);
            push(listenerId);
            loadClassLoader();
            push(targetJavaClassName);
            push(name);
            push(desc);
            loadThisOrPushNullIfIsStatic();
            invokeStatic(ASM_TYPE_SPY, ASM_METHOD_Spy$spyMethodOnBefore);
            swap();
            storeArgArray();
            pop();
            processControl();
            isMethodEnter = true;
            mark(beginLabel);
          }
        });
      }

      /**
             * 是否抛出异常返回(通过字节码判断)
             *
             * @param opcode 操作码
             * @return true:以抛异常形式返回 / false:非抛异常形式返回(return)
             */
      private boolean isThrow(int opcode) {
        return opcode == ATHROW;
      }

      /**
             * 加载返回值
             * @param opcode 操作吗
             */
      private void loadReturn(int opcode) {
        switch (opcode) {
          case RETURN:
          {
            pushNull();
            break;
          }
          case ARETURN:
          {
            dup();
            break;
          }
          case LRETURN:
          case DRETURN:
          {
            dup2();
            box(Type.getReturnType(methodDesc));
            break;
          }
          default:
          {
            dup();
            box(Type.getReturnType(methodDesc));
            break;
          }
        }
      }

      @Override protected void onMethodExit(final int opcode) {
        if (!isThrow(opcode)) {
          codeLockForTracing.lock(new CodeLock.Block() {
            @Override public void code() {
              loadReturn(opcode);
              push(namespace);
              push(listenerId);
              invokeStatic(ASM_TYPE_SPY, ASM_METHOD_Spy$spyMethodOnReturn);
              processControl();
            }
          });
        }
      }

      /**
             * 加载异常
             */
      private void loadThrow() {
        dup();
      }

      @Override public void visitMaxs(int maxStack, int maxLocals) {
        mark(endLabel);
        visitTryCatchBlock(beginLabel, endLabel, mark(), ASM_TYPE_THROWABLE.getInternalName());
        codeLockForTracing.lock(new CodeLock.Block() {
          @Override public void code() {
            loadThrow();
            push(namespace);
            push(listenerId);
            invokeStatic(ASM_TYPE_SPY, ASM_METHOD_Spy$spyMethodOnThrows);
            processControl();
          }
        });
        throwException();
        super.visitMaxs(maxStack, maxLocals);
      }

      private int tracingCurrentLineNumber = -1;

      @Override public void visitLineNumber(final int lineNumber, Label label) {
        if (isMethodEnter && isLineEnable) {
          codeLockForTracing.lock(new CodeLock.Block() {
            @Override public void code() {
              push(lineNumber);
              push(namespace);
              push(listenerId);
              invokeStatic(ASM_TYPE_SPY, ASM_METHOD_Spy$spyMethodOnLine);
            }
          });
        }
        super.visitLineNumber(lineNumber, label);
        this.tracingCurrentLineNumber = lineNumber;
      }

      @Override public void visitInsn(int opcode) {
        super.visitInsn(opcode);
        codeLockForTracing.code(opcode);
      }

      @Override public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
        if (!isMethodEnter || !isCallEnable || codeLockForTracing.isLock()) {
          super.visitMethodInsn(opcode, owner, name, desc, itf);
          return;
        }
        if (hasCallBefore) {
          codeLockForTracing.lock(new CodeLock.Block() {
            @Override public void code() {
              push(tracingCurrentLineNumber);
              push(toJavaClassName(owner));
              push(name);
              push(desc);
              push(namespace);
              push(listenerId);
              invokeStatic(ASM_TYPE_SPY, ASM_METHOD_Spy$spyMethodOnCallBefore);
            }
          });
        }
        if (!hasCallThrows) {
          super.visitMethodInsn(opcode, owner, name, desc, itf);
          codeLockForTracing.lock(new CodeLock.Block() {
            @Override public void code() {
              push(namespace);
              push(listenerId);
              invokeStatic(ASM_TYPE_SPY, ASM_METHOD_Spy$spyMethodOnCallReturn);
            }
          });
          return;
        }
        final Label tracingBeginLabel = new Label();
        final Label tracingEndLabel = new Label();
        final Label tracingFinallyLabel = new Label();
        mark(tracingBeginLabel);
        super.visitMethodInsn(opcode, owner, name, desc, itf);
        mark(tracingEndLabel);
        if (hasCallReturn) {
          codeLockForTracing.lock(new CodeLock.Block() {
            @Override public void code() {
              push(namespace);
              push(listenerId);
              invokeStatic(ASM_TYPE_SPY, ASM_METHOD_Spy$spyMethodOnCallReturn);
            }
          });
        }
        goTo(tracingFinallyLabel);
        catchException(tracingBeginLabel, tracingEndLabel, ASM_TYPE_THROWABLE);
        codeLockForTracing.lock(new CodeLock.Block() {
          @Override public void code() {
            dup();
            invokeVirtual(ASM_TYPE_OBJECT, ASM_METHOD_Object$getClass);
            invokeVirtual(ASM_TYPE_CLASS, ASM_METHOD_Class$getName);
            push(namespace);
            push(listenerId);
            invokeStatic(ASM_TYPE_SPY, ASM_METHOD_Spy$spyMethodOnCallThrows);
          }
        });
        throwException();
        mark(tracingFinallyLabel);
      }

      private final ArrayList<AsmTryCatchBlock> asmTryCatchBlocks = new ArrayList<AsmTryCatchBlock>();

      @Override public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        asmTryCatchBlocks.add(new AsmTryCatchBlock(start, end, handler, type));
      }

      @Override public void visitEnd() {
        for (AsmTryCatchBlock tcb : asmTryCatchBlocks) {
          super.visitTryCatchBlock(tcb.start, tcb.end, tcb.handler, tcb.type);
        }
        super.visitEnd();
      }
    };
  }
}