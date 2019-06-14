package cli;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class GeneralPresenter
{
	public <T, R> T prepare(final Class<T> tClass, final T target, final Class<R> rClass, final Consumer<R> consumer)
	{
		return register(rClass, consumer).proxy(tClass, target);
	}

	public <T> T proxy(final Class<T> tClass, final T target)
	{
		return tClass.cast(Proxy.newProxyInstance(tClass.getClassLoader(), new Class<?>[] { tClass }, (p, m, a) ->
		{
			try
			{
				final Object result = m.invoke(target, a);
				if (!handle(m.getReturnType(), result))
					System.out.println("UNHANDLED: " + result.getClass().getName() + " - " + handlers);
				return result;
			}
			catch (final Exception e)
			{
				if (!handle(e.getClass(), e))
					throw e;
				return null;
			}
		}));
	}

	public GeneralPresenter registerAllPublicSingleParameterMethods(final Object o)
	{
		for (final var m : o.getClass().getMethods())
		{
			if (Object.class.equals(m.getDeclaringClass())
				|| m.getParameterCount() != 1
				|| !Modifier.isPublic(m.getModifiers()))
				continue;
			final Class<?> c = m.getParameterTypes()[0];
			handlers.put(m.getParameterTypes()[0], arg ->
			{
				try
				{
					m.invoke(o, arg);
				}
				catch (Exception e)
				{
					throw new RuntimeException(e);
				}
			});
		}
		return this;
	}

	public <T> GeneralPresenter register(final Class<T> tClass, Consumer<T> consumer)
	{
		handlers.put(tClass, consumer);
		return this;
	}

	private boolean handle(final Class<?> rClass, final Object o) throws Exception
	{
		if (!rClass.isInstance(o))
			throw new ClassCastException();
		final Consumer<?> c = handlers.get(rClass);
		if (c == null)
			return false;
		((Consumer<Object>)c).accept(o);
		return true;
	}

	private Map<Class<?>, Consumer<?>> handlers = new HashMap<>();
}
