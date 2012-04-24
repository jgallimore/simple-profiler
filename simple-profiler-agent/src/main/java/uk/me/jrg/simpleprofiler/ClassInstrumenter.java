package uk.me.jrg.simpleprofiler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.regex.Pattern;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

public class ClassInstrumenter implements ClassFileTransformer {

	private final String[] regexs;

	public static class ModifierMethodWriter extends AdviceAdapter {

		private String className;
		private String methodName;
		

		public ModifierMethodWriter(String className, int api, MethodVisitor mv, int access, String name, String desc) {
			super(api, mv, access, name, desc);
			this.className = className;
			this.methodName = name;
		}

		@Override
		protected void onMethodEnter() {
			super.onMethodEnter();
			super.visitMethodInsn(INVOKESTATIC, "uk/me/jrg/simpleprofiler/Recorder", "getInstance", "()Luk/me/jrg/simpleprofiler/Recorder;");
			super.visitLdcInsn(className);
			super.visitLdcInsn(methodName);
			super.visitMethodInsn(INVOKEVIRTUAL, "uk/me/jrg/simpleprofiler/Recorder", "methodStarted", "(Ljava/lang/String;Ljava/lang/String;)V");
		}

		@Override
		protected void onMethodExit(int opcode) {
			super.onMethodExit(opcode);
			super.visitMethodInsn(INVOKESTATIC, "uk/me/jrg/simpleprofiler/Recorder", "getInstance", "()Luk/me/jrg/simpleprofiler/Recorder;");
			super.visitLdcInsn(className);
			super.visitLdcInsn(methodName);
			super.visitMethodInsn(INVOKEVIRTUAL, "uk/me/jrg/simpleprofiler/Recorder", "methodComplete", "(Ljava/lang/String;Ljava/lang/String;)V");
		}
	}

	public static class ModifierClassWriter extends ClassVisitor {
		private String className;
		private int api;

		public ModifierClassWriter(int api, ClassWriter cv) {
			super(api, cv);
			this.api = api;
		}

		@Override
		public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
			this.className = name;
			super.visit(version, access, name, signature, superName, interfaces);
		}

		@Override
		public void visitOuterClass(String owner, String name, String desc) {
			this.className = name;
			super.visitOuterClass(owner, name, desc);
		}

		@Override
		public void visitInnerClass(String name, String outerName, String innerName, int access) {
			this.className = name;
			super.visitInnerClass(name, outerName, innerName, access);
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
			MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
			ModifierMethodWriter mvw = new ModifierMethodWriter(className, api, mv, access, name, desc);
			return mvw;
		}
	}

	public ClassInstrumenter(String[] regexs) {
		this.regexs = regexs;
	}

	public byte[] instrument(InputStream is) throws IOException {
		ClassReader classReader = new ClassReader(is);
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

		ModifierClassWriter mcw = new ModifierClassWriter(Opcodes.ASM4, cw);
		classReader.accept(mcw, 0);
		return cw.toByteArray();
	}

	public byte[] transform(ClassLoader cl, String name, Class<?> cls, ProtectionDomain protectionDomain, byte[] buffer) throws IllegalClassFormatException {
		for (String regex : regexs) {
			if (Pattern.matches(regex, name)) {
				try {
					InputStream is = new ByteArrayInputStream(buffer);
					return instrument(is);
				} catch (IOException e) {
					throw new IllegalClassFormatException(e.getMessage());
				}
			}
		}
		
		return buffer;
	}
}
