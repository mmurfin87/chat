package cli;

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
				handle(m.getReturnType(), result);
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
